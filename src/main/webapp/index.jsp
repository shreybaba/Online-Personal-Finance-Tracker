<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- Logged-in visitors get a link straight to their own dashboard --%>
<c:if test="${not empty sessionScope.user}">
    <c:choose>
        <c:when test="${sessionScope.user.role == 'ADMIN'}"><c:set var="homeUrl" value="${ctx}/admin/dashboard"/></c:when>
        <c:when test="${sessionScope.user.role == 'ADVISOR'}"><c:set var="homeUrl" value="${ctx}/advisor/dashboard"/></c:when>
        <c:otherwise><c:set var="homeUrl" value="${ctx}/user/dashboard"/></c:otherwise>
    </c:choose>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Finance. - Personal Finance Tracker</title>
    <meta name="description" content="Track expenses, set category budgets, get advice from financial advisors and see clear spending reports.">

    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    <link rel="stylesheet" href="${ctx}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${ctx}/assets/css/responsive.css">
    <link rel="stylesheet" href="${ctx}/assets/css/landing.css">
    <link rel="shortcut icon" href="${ctx}/assets/images/favicon.ico" type="image/x-icon">
</head>
<body>

    <header class="app-navbar">
        <a href="${ctx}/" class="navbar-brand">Finance.</a>

        <nav class="landing-nav-links">
            <a href="#features" class="nav-anchor">Features</a>
            <a href="#roles" class="nav-anchor">Who it's for</a>
            <a href="#how" class="nav-anchor">How it works</a>
            <a href="${ctx}/docs">Docs</a>
            <c:choose>
                <c:when test="${not empty homeUrl}">
                    <a href="${homeUrl}" class="btn btn-primary btn-sm">Open dashboard</a>
                </c:when>
                <c:otherwise>
                    <a href="${ctx}/login">Sign in</a>
                    <a href="${ctx}/register" class="btn btn-primary btn-sm">Get started</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </header>

    <main class="landing">

        <!-- Hero -->
        <section class="hero">
            <div>
                <p class="section-eyebrow">Personal finance tracker</p>
                <h1 class="hero-title">Manage your money with clarity.</h1>
                <p class="hero-text">
                    Record every expense, set budgets for each category, and see at a glance
                    where your money goes. Advisors can review your spending and send you advice.
                </p>

                <div class="hero-actions">
                    <c:choose>
                        <c:when test="${not empty homeUrl}">
                            <a href="${homeUrl}" class="btn btn-primary">Open your dashboard</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${ctx}/register" class="btn btn-primary">Create free account</a>
                            <a href="${ctx}/login" class="btn btn-secondary">Sign in</a>
                        </c:otherwise>
                    </c:choose>
                </div>

                <ul class="hero-points">
                    <li>Weekly, monthly &amp; annual budgets</li>
                    <li>Category reports</li>
                    <li>Advisor recommendations</li>
                </ul>
            </div>

            <!-- Miniature of the user dashboard, built from the same components -->
            <div class="preview" aria-label="Example dashboard preview">
                <div class="preview-bar">
                    <span>Dashboard</span>
                    <span class="badge">Example data</span>
                </div>

                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-label">Total Expenses</div>
                        <div class="stat-value">₹6,101</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Budget</div>
                        <div class="stat-value">₹5,000</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Remaining</div>
                        <div class="stat-value">₹1,349</div>
                    </div>
                </div>

                <div class="preview-grid">
                    <div class="preview-panel">
                        <div class="preview-panel-title">Spending by Category</div>
                        <div class="chart-bar-group">
                            <div class="chart-bar-row">
                                <div class="chart-bar-label"><span>Groceries</span><span class="font-mono">₹2,450</span></div>
                                <div class="chart-bar-bg"><div class="chart-bar-fill" style="width: 40%;"></div></div>
                            </div>
                            <div class="chart-bar-row">
                                <div class="chart-bar-label"><span>Utilities</span><span class="font-mono">₹1,800</span></div>
                                <div class="chart-bar-bg"><div class="chart-bar-fill" style="width: 30%;"></div></div>
                            </div>
                            <div class="chart-bar-row">
                                <div class="chart-bar-label"><span>Dining</span><span class="font-mono">₹1,200</span></div>
                                <div class="chart-bar-bg"><div class="chart-bar-fill" style="width: 20%;"></div></div>
                            </div>
                            <div class="chart-bar-row">
                                <div class="chart-bar-label"><span>Transport</span><span class="font-mono">₹650</span></div>
                                <div class="chart-bar-bg"><div class="chart-bar-fill" style="width: 11%;"></div></div>
                            </div>
                        </div>
                    </div>

                    <div class="preview-panel">
                        <div class="preview-panel-title">Budget Status</div>
                        <p style="font-size: 0.8rem;" class="text-muted">Spent <span class="font-mono">₹3,650</span> of <span class="font-mono">₹5,000</span></p>
                        <div class="progress-container">
                            <div class="progress-bar" style="width: 73%;"></div>
                        </div>
                        <div class="advice-card">
                            <div class="advice-meta"><span>From your advisor</span></div>
                            <div class="advice-content">Dining is over budget this month — try cooking at home twice a week.</div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Features -->
        <section id="features" class="landing-section">
            <p class="section-eyebrow">Features</p>
            <h2 class="section-title">Everything you need to stay on budget</h2>
            <p class="section-lead">Simple tools that cover your day-to-day spending, from the first expense to the yearly report.</p>

            <div class="feature-grid">
                <div class="card feature-card">
                    <div class="feature-num">01</div>
                    <div class="card-title">Track expenses</div>
                    <p>Log each transaction with a category, amount and date. Edit or delete it any time.</p>
                </div>
                <div class="card feature-card">
                    <div class="feature-num">02</div>
                    <div class="card-title">Set budgets</div>
                    <p>Give each category a weekly, monthly or annual limit and watch the progress bar fill up.</p>
                </div>
                <div class="card feature-card">
                    <div class="feature-num">03</div>
                    <div class="card-title">View reports</div>
                    <p>Filter by date range and category to see totals, averages and budget vs. actual spending.</p>
                </div>
                <div class="card feature-card">
                    <div class="feature-num">04</div>
                    <div class="card-title">Get advice</div>
                    <p>Advisors review your spending and send recommendations straight to your dashboard.</p>
                </div>
            </div>
        </section>

        <!-- Roles -->
        <section id="roles" class="landing-section">
            <p class="section-eyebrow">Who it's for</p>
            <h2 class="section-title">One app, three roles</h2>
            <p class="section-lead">Each account type gets its own dashboard with the tools it needs.</p>

            <div class="role-grid">
                <div class="card role-card">
                    <span class="badge badge-info">User</span>
                    <div class="card-title">Manage your own money</div>
                    <ul>
                        <li>Add, edit and delete expenses</li>
                        <li>Set category budgets</li>
                        <li>See reports and advisor advice</li>
                        <li>Send feedback or ask for help</li>
                    </ul>
                </div>
                <div class="card role-card">
                    <span class="badge badge-info">Advisor</span>
                    <div class="card-title">Guide your clients</div>
                    <ul>
                        <li>Browse registered users</li>
                        <li>Review a client's expenses</li>
                        <li>Send personalised advice</li>
                        <li>Track the advice you've issued</li>
                    </ul>
                </div>
                <div class="card role-card">
                    <span class="badge badge-info">Admin</span>
                    <div class="card-title">Run the system</div>
                    <ul>
                        <li>See system-wide totals</li>
                        <li>Manage user accounts</li>
                        <li>Review feedback tickets</li>
                        <li>Mark tickets in review or resolved</li>
                    </ul>
                </div>
            </div>
        </section>

        <!-- How it works -->
        <section id="how" class="landing-section">
            <p class="section-eyebrow">How it works</p>
            <h2 class="section-title">Up and running in minutes</h2>
            <p class="section-lead">No spreadsheets, no setup. Sign up and start tracking.</p>

            <div class="steps">
                <div class="step">
                    <div class="step-num">Step 1</div>
                    <h3>Create your account</h3>
                    <p>Register with your name and email, then sign in to your personal dashboard.</p>
                </div>
                <div class="step">
                    <div class="step-num">Step 2</div>
                    <h3>Add expenses &amp; budgets</h3>
                    <p>Record what you spend and set limits for the categories that matter to you.</p>
                </div>
                <div class="step">
                    <div class="step-num">Step 3</div>
                    <h3>Review and improve</h3>
                    <p>Check your reports, follow your advisor's advice, and stay within budget.</p>
                </div>
            </div>
        </section>

        <!-- Call to action -->
        <section class="landing-section">
            <div class="card cta">
                <div>
                    <h2>Start tracking your spending today.</h2>
                    <p>Free to use. Your data stays in your account.</p>
                </div>
                <c:choose>
                    <c:when test="${not empty homeUrl}">
                        <a href="${homeUrl}" class="btn btn-primary">Open dashboard</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${ctx}/register" class="btn btn-primary">Get started</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>

    </main>

    <footer class="app-footer">
        <p>&copy; 2026 Finance. Online Personal Finance Management System</p>
    </footer>

    <script src="${ctx}/assets/js/main.js"></script>
</body>
</html>
