package com.finance.service;

import com.finance.exception.DatabaseException;
import com.finance.model.Budget;
import com.finance.model.CategorySummary;
import com.finance.model.Expense;
import com.finance.model.User;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * AI financial advisor service for normal users.
 * Uses Gemini API when GEMINI_API_KEY (or ai.api.key in db.properties) is configured,
 * and falls back to a data-driven local financial analyzer using the user's real expenses and budgets.
 */
public class AiAdvisorService {

    private static final String GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    private final ExpenseService expenseService;
    private final BudgetService budgetService;
    private final ReportService reportService;
    private final HttpClient httpClient;

    public static class AiReply {
        private final String message;
        private final String mode; // "gemini" or "local"

        public AiReply(String message, String mode) {
            this.message = message;
            this.mode = mode;
        }

        public String getMessage() {
            return message;
        }

        public String getMode() {
            return mode;
        }
    }

    public AiAdvisorService() {
        this.expenseService = new ExpenseService();
        this.budgetService = new BudgetService();
        this.reportService = new ReportService();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    public AiReply ask(User user, String prompt) throws DatabaseException {
        if (prompt == null || prompt.trim().isEmpty()) {
            return new AiReply("Please enter a question about your expenses, budgets, or savings goals.", "local");
        }

        String userId = user.getId();
        List<Expense> expenses = expenseService.getExpensesByUserId(userId);
        List<Budget> budgets = budgetService.getUserBudgetsWithSpent(userId);
        ReportService.ReportData report = reportService.generateReport(userId, null, null, null);

        String contextSummary = buildFinancialContext(user, expenses, budgets, report);
        String apiKey = resolveApiKey();

        if (apiKey != null && !apiKey.isEmpty()) {
            String geminiText = callGemini(apiKey, contextSummary, prompt.trim());
            if (geminiText != null && !geminiText.isEmpty()) {
                return new AiReply(geminiText, "gemini");
            }
        }

        String localText = buildLocalAdvice(user, prompt.trim(), expenses, budgets, report);
        return new AiReply(localText, "local");
    }

    private String resolveApiKey() {
        String envKey = System.getenv("GEMINI_API_KEY");
        if (envKey != null && !envKey.trim().isEmpty()) {
            return envKey.trim();
        }
        try (InputStream in = AiAdvisorService.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                String propKey = props.getProperty("ai.api.key");
                if (propKey != null && !propKey.trim().isEmpty()) {
                    return propKey.trim();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private String buildFinancialContext(User user, List<Expense> expenses, List<Budget> budgets,
                                         ReportService.ReportData report) {
        StringBuilder sb = new StringBuilder();
        sb.append("User: ").append(user.getName()).append("\n");
        sb.append("Total spending: INR ").append(report.getTotalSpending()).append("\n");
        sb.append("Average expense: INR ").append(report.getAvgExpense()).append("\n");
        sb.append("Expense count: ").append(expenses.size()).append("\n");

        List<CategorySummary> categories = report.getCategoryReportList();
        if (categories != null && !categories.isEmpty()) {
            sb.append("Category breakdown:\n");
            for (CategorySummary cs : categories) {
                sb.append("- ").append(cs.getCategory()).append(": INR ")
                  .append(cs.getAmount()).append(" (").append(cs.getPercentage()).append("%)\n");
            }
        }

        if (budgets != null && !budgets.isEmpty()) {
            sb.append("Budgets:\n");
            for (Budget b : budgets) {
                sb.append("- ").append(b.getCategory()).append(" (").append(b.getPeriod()).append("): spent INR ")
                  .append(b.getSpent()).append(" / limit INR ").append(b.getAmount())
                  .append(" (").append(b.getPercentage()).append("%)\n");
            }
        } else {
            sb.append("Budgets: none configured yet.\n");
        }
        return sb.toString();
    }

    private String callGemini(String apiKey, String context, String userPrompt) {
        try {
            String fullPrompt = "You are a concise, helpful personal finance AI advisor inside a Personal Finance Tracker app. "
                    + "Use INR (₹) for amounts. Base your advice on the user's real data below, keep it practical and under 140 words.\n\n"
                    + "User Financial Snapshot:\n" + context + "\nUser Question: " + userPrompt;

            String payload = "{\"contents\":[{\"parts\":[{\"text\":\"" + escapeJson(fullPrompt) + "\"}]}]}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_ENDPOINT + apiKey))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return extractFirstGeminiText(response.body());
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private String buildLocalAdvice(User user, String prompt, List<Expense> expenses,
                                    List<Budget> budgets, ReportService.ReportData report) {
        String q = prompt.toLowerCase(Locale.ROOT);
        BigDecimal totalSpent = report.getTotalSpending() != null ? report.getTotalSpending() : BigDecimal.ZERO;
        List<CategorySummary> categories = report.getCategoryReportList();

        CategorySummary topCategory = (categories != null && !categories.isEmpty()) ? categories.get(0) : null;

        int overBudgetCount = 0;
        int nearLimitCount = 0;
        StringBuilder budgetAlerts = new StringBuilder();
        BigDecimal totalBudget = BigDecimal.ZERO;

        if (budgets != null) {
            for (Budget b : budgets) {
                if (b.getAmount() != null) {
                    totalBudget = totalBudget.add(b.getAmount());
                }
                if (b.getSpent() != null && b.getAmount() != null && b.getSpent().compareTo(b.getAmount()) > 0) {
                    overBudgetCount++;
                    budgetAlerts.append(b.getCategory()).append(" (₹").append(b.getSpent())
                                .append(" / ₹").append(b.getAmount()).append("), ");
                } else if (b.getPercentage() >= 80.0) {
                    nearLimitCount++;
                    budgetAlerts.append(b.getCategory()).append(" (").append(b.getPercentage()).append("% used), ");
                }
            }
        }

        if (q.contains("budget") || q.contains("limit") || q.contains("over")) {
            if (budgets == null || budgets.isEmpty()) {
                return "You haven't set any category budgets yet. Start by creating a monthly budget for your top spending categories"
                        + (topCategory != null ? "—especially " + topCategory.getCategory() + " (currently ₹" + topCategory.getAmount() + ")" : "")
                        + ". You can also send a request to a human advisor on the right for a tailored budget plan.";
            }
            if (overBudgetCount > 0 || nearLimitCount > 0) {
                String alerts = budgetAlerts.substring(0, budgetAlerts.length() - 2);
                return "Budget check: You have " + overBudgetCount + " over-budget and " + nearLimitCount
                        + " near-limit (>=80%) categories: " + alerts + ". Consider pausing discretionary spending in these categories until the next cycle.";
            }
            return "Great job! All " + budgets.size() + " of your budgets are under the 80% warning threshold. Total budgeted: ₹"
                    + totalBudget + ", total spent across all expenses: ₹" + totalSpent + ".";
        }

        if (q.contains("save") || q.contains("saving") || q.contains("reduce") || q.contains("cut")) {
            if (topCategory != null) {
                BigDecimal tenPercent = topCategory.getAmount()
                        .multiply(new BigDecimal("0.15"))
                        .setScale(0, RoundingMode.HALF_UP);
                return "Your largest expense category is " + topCategory.getCategory() + " at ₹"
                        + topCategory.getAmount() + " (" + topCategory.getPercentage()
                        + "% of your spending). Trimming this category by 15% would save about ₹" + tenPercent
                        + ". Try applying the 50/30/20 rule and setting a tighter limit in Budgets.";
            }
            return "Once you log a few expenses, I can pinpoint which category gives you the fastest savings. Meanwhile, aim to allocate 20% of income to savings first.";
        }

        if (q.contains("advisor") || q.contains("human") || q.contains("help") || q.contains("request")) {
            return "I can give instant insights from your expenses and budgets here, and for personalized long-term planning you can submit a request in the 'Request Human Advisor' form on this page—an advisor will review your expenses and reply directly.";
        }

        // Default: holistic spending & budget overview
        if (expenses.isEmpty()) {
            return "Hi " + user.getName() + "! You don't have any recorded expenses yet. Add a few expenses and budgets, then ask me to analyze your spending, check budget health, or suggest savings.";
        }

        StringBuilder reply = new StringBuilder();
        reply.append("Here is your snapshot, ").append(user.getName()).append(": You've logged ")
             .append(expenses.size()).append(" expense(s) totaling ₹").append(totalSpent)
             .append(" (avg ₹").append(report.getAvgExpense()).append(").");

        if (topCategory != null) {
            reply.append(" Top category: ").append(topCategory.getCategory())
                 .append(" (₹").append(topCategory.getAmount()).append(", ")
                 .append(topCategory.getPercentage()).append("%).");
        }

        if (overBudgetCount > 0) {
            reply.append(" Alert: ").append(overBudgetCount).append(" budget(s) exceeded.");
        } else if (nearLimitCount > 0) {
            reply.append(" Watch out: ").append(nearLimitCount).append(" budget(s) are at 80%+.");
        } else if (budgets != null && !budgets.isEmpty()) {
            reply.append(" All ").append(budgets.size()).append(" budgets are currently on track.");
        }

        reply.append(" Ask me about 'budgets', 'how to save', or send a question to a human advisor anytime.");
        return reply.toString();
    }

    private static String escapeJson(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"':  sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:   sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String extractFirstGeminiText(String json) {
        int keyIdx = json.indexOf("\"text\"");
        if (keyIdx < 0) return null;
        int colonIdx = json.indexOf(':', keyIdx + 6);
        if (colonIdx < 0) return null;
        int firstQuote = json.indexOf('"', colonIdx + 1);
        if (firstQuote < 0) return null;

        StringBuilder out = new StringBuilder();
        boolean escape = false;
        for (int i = firstQuote + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                switch (c) {
                    case 'n': out.append('\n'); break;
                    case 'r': out.append('\r'); break;
                    case 't': out.append('\t'); break;
                    case '"': out.append('"'); break;
                    case '\\': out.append('\\'); break;
                    default: out.append(c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                break;
            } else {
                out.append(c);
            }
        }
        return out.toString().trim();
    }
}
