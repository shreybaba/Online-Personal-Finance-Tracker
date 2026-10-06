<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
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
    <title>Docs - Finance.</title>
    <meta name="description" content="Technical documentation: tech stack, architecture, routes, database and security of the Finance. personal finance tracker.">

    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    <link rel="stylesheet" href="${ctx}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${ctx}/assets/css/responsive.css">
    <link rel="stylesheet" href="${ctx}/assets/css/landing.css">
    <link rel="stylesheet" href="${ctx}/assets/css/docs.css">
    <link rel="shortcut icon" href="${ctx}/assets/images/favicon.ico" type="image/x-icon">
</head>
<body>

    <header class="app-navbar">
        <a href="${ctx}/" class="navbar-brand">Finance.</a>

        <nav class="landing-nav-links">
            <a href="${ctx}/" class="nav-anchor">Home</a>
            <a href="${ctx}/docs" style="color: var(--text-primary);">Docs</a>
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
        <div class="docs-layout">

            <aside class="docs-toc" aria-label="On this page">
                <p class="section-eyebrow">On this page</p>
                <ol>
                    <li><a href="#overview">Overview</a></li>
                    <li><a href="#stack">Tech stack</a></li>
                    <li><a href="#architecture">Architecture</a></li>
                    <li><a href="#request-flow">Request flow</a></li>
                    <li><a href="#structure">Project structure</a></li>
                    <li><a href="#routes">Routes</a></li>
                    <li><a href="#database">Database</a></li>
                    <li><a href="#rules">Business rules</a></li>
                    <li><a href="#security">Security</a></li>
                    <li><a href="#frontend">Frontend</a></li>
                    <li><a href="#setup">Setup &amp; running</a></li>
                    <li><a href="#limitations">Known limitations</a></li>
                </ol>
            </aside>

            <article class="docs-content">

                <div class="docs-intro">
                    <p class="section-eyebrow">Documentation</p>
                    <h1>How Finance. is built</h1>
                    <p>A walkthrough of the tech stack, architecture, URL routes, database and security model of this
                        personal finance tracker.</p>
                </div>

                <!-- Overview -->
                <section id="overview" class="docs-section">
                    <h2>Overview</h2>
                    <p>Finance. is a server-rendered web application for tracking personal spending. It has three roles,
                        and each one gets its own dashboard:</p>
                    <div class="table-container">
                        <table class="table">
                            <thead><tr><th>Role</th><th>What they can do</th></tr></thead>
                            <tbody>
                                <tr><td><span class="badge badge-info">USER</span></td><td>Add, edit and delete expenses; set weekly, monthly or annual budgets per category; view reports; read advisor advice; change password; send feedback.</td></tr>
                                <tr><td><span class="badge badge-info">ADVISOR</span></td><td>Browse users with the USER role, inspect a user's expenses, send advice, and delete advice they wrote.</td></tr>
                                <tr><td><span class="badge badge-info">ADMIN</span></td><td>See system totals, list and delete user accounts, and move feedback tickets between PENDING, IN_REVIEW and RESOLVED.</td></tr>
                            </tbody>
                        </table>
                    </div>
                </section>

                <!-- Stack -->
                <section id="stack" class="docs-section">
                    <h2>Tech stack</h2>
                    <p>A classic Java EE style stack, with no framework: plain servlets, JSP views and JDBC.</p>
                    <div class="stack-grid">
                        <div class="stack-item"><div class="stat-label">Language</div><strong>Java 21</strong><span>Compiled with <code>maven.compiler.release=21</code></span></div>
                        <div class="stack-item"><div class="stat-label">Web API</div><strong>Jakarta Servlet 6.1</strong><span>Servlets and filters via annotations</span></div>
                        <div class="stack-item"><div class="stat-label">Server</div><strong>Apache Tomcat 11</strong><span>Deployed at context path <code>/finance</code></span></div>
                        <div class="stack-item"><div class="stat-label">Views</div><strong>JSP + JSTL 3.0</strong><span>Server-rendered pages, <code>c:</code> tags and EL</span></div>
                        <div class="stack-item"><div class="stat-label">Data access</div><strong>JDBC</strong><span>Prepared statements, no ORM</span></div>
                        <div class="stack-item"><div class="stat-label">Database</div><strong>MySQL</strong><span>Driver <code>mysql-connector-j 9.4.0</code></span></div>
                        <div class="stack-item"><div class="stat-label">Build</div><strong>Maven</strong><span>WAR packaging → <code>target/finance.war</code></span></div>
                        <div class="stack-item"><div class="stat-label">Styling</div><strong>Plain CSS</strong><span>Design tokens, no CSS framework</span></div>
                        <div class="stack-item"><div class="stat-label">Scripts</div><strong>Vanilla JavaScript</strong><span>Form validation, mobile menu, budget bars</span></div>
                    </div>
                </section>

                <!-- Architecture -->
                <section id="architecture" class="docs-section">
                    <h2>Architecture</h2>
                    <p>The backend is split into layers. Each layer only talks to the one directly below it, which keeps
                        SQL out of the servlets and HTTP code out of the business logic.</p>

                    <div class="layers">
                        <div class="layer"><strong>JSP views</strong><span>Render HTML from request attributes</span></div>
                        <div class="layer-arrow">↑ attributes &nbsp;·&nbsp; forms ↓</div>
                        <div class="layer"><strong>Filters</strong><span>UTF-8, flash messages, login &amp; role checks</span></div>
                        <div class="layer-arrow">↓</div>
                        <div class="layer"><strong>Servlets</strong><span>Map URLs to actions, choose the view</span></div>
                        <div class="layer-arrow">↓</div>
                        <div class="layer"><strong>Services</strong><span>Validation and business rules</span></div>
                        <div class="layer-arrow">↓</div>
                        <div class="layer"><strong>DAOs</strong><span>All SQL, one class per table</span></div>
                        <div class="layer-arrow">↓</div>
                        <div class="layer"><strong>JDBC → MySQL</strong><span><code>DBConnection</code> opens connections</span></div>
                    </div>

                    <p>Invalid input is reported with a <code>ValidationException</code>, whose message is safe to show
                        to the user. Database failures become a <code>ServletException</code>, which Tomcat turns into
                        the 500 error page.</p>
                </section>

                <!-- Request flow -->
                <section id="request-flow" class="docs-section">
                    <h2>Request flow</h2>
                    <p>What happens when a user submits the <em>Add Expense</em> form:</p>
                    <ol class="flow">
                        <li><span><code>RequestSetupFilter</code> reads the form as UTF-8 and moves any one-time message from the previous request into the page.</span></li>
                        <li><span><code>AuthFilter</code> checks there is a logged-in user with the <code>USER</code> role. If not, it redirects to <code>/login</code> or returns 403.</span></li>
                        <li><span><code>ExpenseServlet</code> reads <code>category</code>, <code>amount</code> and <code>date</code>, takes the user ID from the session, and calls the service.</span></li>
                        <li><span><code>ExpenseService</code> validates the input (category up to 15 characters, amount above 0 with at most 2 decimals, a valid date) and generates an ID.</span></li>
                        <li><span><code>ExpenseDao</code> runs <code>INSERT INTO expenses ...</code> with bound parameters.</span></li>
                        <li><span>The servlet stores "Expense added." as a flash message and <strong>redirects</strong> to <code>/user/expenses</code>, so refreshing the page can't submit the form twice (Post/Redirect/Get).</span></li>
                        <li><span>The list page loads, the filter exposes the message, and <code>alerts.jsp</code> displays it once.</span></li>
                    </ol>
                </section>

                <!-- Structure -->
                <section id="structure" class="docs-section">
                    <h2>Project structure</h2>
<pre class="docs-pre">Online-Personal-Finance-Tracker/
├── pom.xml                      Maven build (WAR), dependencies
├── db/schema.sql                Creates the database and 5 tables
└── src/main/
    ├── java/com/finance/
    │   ├── DBConnection.java    JDBC connections (db.properties / env vars)
    │   ├── model/               User, Expense, Budget, Advice, Feedback,
    │   │                        CategoryTotal, BudgetComparison
    │   ├── dao/                 UserDao, ExpenseDao, BudgetDao, AdviceDao, FeedbackDao
    │   ├── service/             AuthService, ExpenseService, BudgetService,
    │   │                        ReportService, AdviceService, FeedbackService,
    │   │                        UserService, ValidationException
    │   ├── servlet/             BaseServlet + one servlet per area
    │   ├── filter/              AuthFilter, RequestSetupFilter
    │   └── util/                IdGenerator, Validator, Money, Flash
    ├── resources/
    │   ├── db.properties        Your DB settings (git-ignored)
    │   └── db.properties.example
    └── webapp/
        ├── index.jsp            Landing page
        ├── assets/              css/, js/, images/
        └── WEB-INF/
            ├── web.xml          Welcome file and error pages
            └── views/           auth/, user/, advisor/, admin/, common/, error/, docs.jsp</pre>

                    <h3>Key classes</h3>
                    <div class="table-container">
                        <table class="table">
                            <thead><tr><th>Class</th><th>Responsibility</th></tr></thead>
                            <tbody>
                                <tr><td><code>BaseServlet</code></td><td>Shared helpers: <code>currentUser()</code>, <code>render()</code> (forward to a JSP), <code>redirect()</code>, and the home page for each role.</td></tr>
                                <tr><td><code>AuthFilter</code></td><td>Protects <code>/user/*</code>, <code>/feedback/*</code>, <code>/advisor/*</code> and <code>/admin/*</code>. Adds <code>Cache-Control: no-store</code> to private pages.</td></tr>
                                <tr><td><code>RequestSetupFilter</code></td><td>Sets UTF-8 request encoding and moves flash messages from the session into the request.</td></tr>
                                <tr><td><code>AuthService</code></td><td>Login, registration and password change.</td></tr>
                                <tr><td><code>BudgetService</code></td><td>Saves budgets and works out how much was spent in each budget's current period.</td></tr>
                                <tr><td><code>ReportService</code></td><td>Builds the filtered report: total, average, category shares and budget comparison.</td></tr>
                                <tr><td><code>UserDao.deleteWithRelatedData</code></td><td>Deletes a user and all their rows in one transaction.</td></tr>
                                <tr><td><code>IdGenerator</code></td><td>Creates IDs such as <code>EXP17912686593063048</code>: a prefix, the time in milliseconds, and 4 random digits.</td></tr>
                            </tbody>
                        </table>
                    </div>
                </section>

                <!-- Routes -->
                <section id="routes" class="docs-section">
                    <h2>Routes</h2>
                    <p>All URLs are relative to the context path <code>/finance</code>. Actions that change data
                        accept only POST; asking for them with GET returns 405.</p>

                    <h3>Public</h3>
                    <div class="table-container">
                        <table class="table">
                            <thead><tr><th>URL</th><th>Method</th><th>Purpose</th></tr></thead>
                            <tbody>
                                <tr><td><code>/</code></td><td>GET</td><td>Landing page</td></tr>
                                <tr><td><code>/docs</code></td><td>GET</td><td>This documentation</td></tr>
                                <tr><td><code>/login</code></td><td>GET, POST</td><td>Sign-in form / authenticate</td></tr>
                                <tr><td><code>/register</code></td><td>GET, POST</td><td>Sign-up form / create the account and sign in</td></tr>
                                <tr><td><code>/logout</code></td><td>GET, POST</td><td>End the session</td></tr>
                            </tbody>
                        </table>
                    </div>

                    <h3>User (role USER)</h3>
                    <div class="table-container">
                        <table class="table">
                            <thead><tr><th>URL</th><th>Method</th><th>Purpose</th></tr></thead>
                            <tbody>
                                <tr><td><code>/user/dashboard</code></td><td>GET</td><td>Totals, recent expenses, category breakdown, budget status, advice</td></tr>
                                <tr><td><code>/user/expenses</code></td><td>GET</td><td>All expenses, newest first</td></tr>
                                <tr><td><code>/user/add-expense</code></td><td>GET, POST</td><td>Form (<code>?id=</code> to edit) / create or update</td></tr>
                                <tr><td><code>/user/delete-expense</code></td><td>POST</td><td>Delete one of your expenses</td></tr>
                                <tr><td><code>/user/budgets</code></td><td>GET</td><td>Budgets with the amount spent this period</td></tr>
                                <tr><td><code>/user/add-budget</code></td><td>POST</td><td>Create a budget, or update it if one exists for the same category and period</td></tr>
                                <tr><td><code>/user/delete-budget</code></td><td>POST</td><td>Delete a budget</td></tr>
                                <tr><td><code>/user/reports</code></td><td>GET</td><td>Report with optional <code>startDate</code>, <code>endDate</code> and <code>category</code></td></tr>
                                <tr><td><code>/user/profile</code></td><td>GET</td><td>Account details</td></tr>
                                <tr><td><code>/user/update-password</code></td><td>POST</td><td>Change password (requires the current one)</td></tr>
                                <tr><td><code>/feedback/submit</code></td><td>POST</td><td>Send feedback to the admins</td></tr>
                            </tbody>
                        </table>
                    </div>

                    <h3>Advisor (role ADVISOR)</h3>
                    <div class="table-container">
                        <table class="table">
                            <thead><tr><th>URL</th><th>Method</th><th>Purpose</th></tr></thead>
                            <tbody>
                                <tr><td><code>/advisor/dashboard</code></td><td>GET</td><td>Advice you've issued, advice count, number of users advised</td></tr>
                                <tr><td><code>/advisor/advice</code></td><td>GET</td><td>Client list; <code>?selectedUserId=</code> shows that user's expenses</td></tr>
                                <tr><td><code>/advisor/send-advice</code></td><td>POST</td><td>Send advice to a USER account</td></tr>
                                <tr><td><code>/advisor/delete-advice</code></td><td>POST</td><td>Delete advice you wrote</td></tr>
                            </tbody>
                        </table>
                    </div>

                    <h3>Admin (role ADMIN)</h3>
                    <div class="table-container">
                        <table class="table">
                            <thead><tr><th>URL</th><th>Method</th><th>Purpose</th></tr></thead>
                            <tbody>
                                <tr><td><code>/admin/dashboard</code></td><td>GET</td><td>User, expense and pending-feedback counts; newest users; latest feedback</td></tr>
                                <tr><td><code>/admin/users</code></td><td>GET</td><td>All accounts</td></tr>
                                <tr><td><code>/admin/delete-user</code></td><td>POST</td><td>Delete a user and all their data (not yourself)</td></tr>
                                <tr><td><code>/admin/feedback</code></td><td>GET</td><td>All feedback tickets</td></tr>
                                <tr><td><code>/admin/update-feedback-status</code></td><td>POST</td><td>Set status to PENDING, IN_REVIEW or RESOLVED</td></tr>
                            </tbody>
                        </table>
                    </div>
                </section>

                <!-- Database -->
                <section id="database" class="docs-section">
                    <h2>Database</h2>
                    <p>Five MySQL tables, created by <code>db/schema.sql</code>. IDs are strings generated by the
                        application. Rows are linked by <code>user_id</code> and <code>advisor_id</code>, which refer to
                        <code>user.id</code>.</p>
                    <div class="table-container">
                        <table class="table">
                            <thead><tr><th>Table</th><th>Columns</th><th>Notes</th></tr></thead>
                            <tbody>
                                <tr><td><code>user</code></td><td>id, name, role, password, email</td><td>Email is unique; role is USER, ADVISOR or ADMIN</td></tr>
                                <tr><td><code>expenses</code></td><td>id, category, amount, date, user_id</td><td>Amount is <code>DECIMAL(10,2)</code>; category is at most 15 characters</td></tr>
                                <tr><td><code>budgets</code></td><td>id, user_id, category, amount, period</td><td>Period is Monthly, Weekly or Annual</td></tr>
                                <tr><td><code>advice</code></td><td>id, advisor_id, message, date, user_id</td><td>Message is at most 200 characters</td></tr>
                                <tr><td><code>feedback</code></td><td>id, user_id, message, status, date</td><td>Status is PENDING, IN_REVIEW or RESOLVED</td></tr>
                            </tbody>
                        </table>
                    </div>
                </section>

                <!-- Rules -->
                <section id="rules" class="docs-section">
                    <h2>Business rules</h2>
                    <ul>
                        <li><strong>Budget spending</strong> only counts expenses in the same category during the budget's current period: Monday to Sunday of this week, this calendar month, or this calendar year.</li>
                        <li><strong>Saving a budget</strong> for a category and period that already has one updates its amount instead of creating a duplicate.</li>
                        <li><strong>Dashboard totals:</strong> Total Expenses is every expense ever recorded. Current Budget is the sum of all budgets. Remaining is that sum minus what has been spent within each budget's period.</li>
                        <li><strong>Reports</strong> show the total, the average per expense, each category's share of the total, and each budget compared with spending in the selected range.</li>
                        <li><strong>Money</strong> is handled with <code>BigDecimal</code> and rounded to 2 decimals; percentages use 1 decimal and are capped at 100 for bar widths.</li>
                        <li><strong>Deleting a user</strong> also deletes their expenses, budgets, feedback, and any advice they sent or received, in one transaction.</li>
                        <li><strong>New feedback</strong> always starts as PENDING, dated today.</li>
                    </ul>
                </section>

                <!-- Security -->
                <section id="security" class="docs-section">
                    <h2>Security</h2>
                    <ul>
                        <li><strong>Role-based access:</strong> <code>AuthFilter</code> limits each area to its role. Visitors who aren't logged in are redirected to the login page; a logged-in user with the wrong role gets 403.</li>
                        <li><strong>Own data only:</strong> every expense and budget query includes <code>WHERE user_id = ?</code>, so changing an ID in the URL or form can't reach another user's records.</li>
                        <li><strong>Identity from the session:</strong> the user and advisor IDs come from the logged-in session, never from hidden form fields.</li>
                        <li><strong>No SQL injection:</strong> all queries use <code>PreparedStatement</code> with bound parameters.</li>
                        <li><strong>Session safety:</strong> a new session is created at login, to prevent session fixation. "Remember me" keeps the session alive for 7 days, and the password is never stored in the session.</li>
                        <li><strong>Login:</strong> emails are compared in lowercase; passwords are compared in constant time and the error message doesn't say which field was wrong.</li>
                        <li><strong>No caching of private pages:</strong> they're sent with <code>Cache-Control: no-store</code>, so pressing Back after logging out doesn't show your data.</li>
                        <li><strong>Validation on the server</strong> as well as in the browser, for every form.</li>
                    </ul>
                </section>

                <!-- Frontend -->
                <section id="frontend" class="docs-section">
                    <h2>Frontend</h2>
                    <ul>
                        <li><strong>JSP views</strong> live under <code>WEB-INF/views</code>, so they can only be reached through a servlet. Shared parts (<code>header</code>, <code>navbar</code>, <code>sidebar</code>, <code>footer</code>, <code>alerts</code>) are included with <code>&lt;jsp:include&gt;</code>.</li>
                        <li><strong>CSS:</strong> <code>style.css</code> holds the design tokens and base components, <code>dashboard.css</code> the app layout, <code>responsive.css</code> the tablet and phone breakpoints, <code>auth.css</code> the login and register pages, and <code>landing.css</code> + <code>docs.css</code> the public pages.</li>
                        <li><strong>JavaScript:</strong> <code>main.js</code> runs the mobile sidebar, <code>validation.js</code> checks forms in the browser, and <code>dashboard.js</code> colours budget bars (amber at 80%, red at 100%), fills in today's date and asks before deleting.</li>
                        <li><strong>Messages:</strong> servlets set <code>errorMessage</code> / <code>successMessage</code>, and <code>common/alerts.jsp</code> shows them.</li>
                    </ul>
                </section>

                <!-- Setup -->
                <section id="setup" class="docs-section">
                    <h2>Setup &amp; running</h2>
                    <ol class="docs-list">
                        <li>Create the database and tables:</li>
                    </ol>
<pre class="docs-pre">mysql -u root -p &lt; db/schema.sql</pre>
                    <ol class="docs-list" start="2">
                        <li>Copy <code>src/main/resources/db.properties.example</code> to <code>db.properties</code> and set your MySQL password. Alternatively, set the <code>DB_URL</code>, <code>DB_USER</code> and <code>DB_PASSWORD</code> environment variables, which take priority.</li>
                        <li>Build the WAR:</li>
                    </ol>
<pre class="docs-pre">mvn clean package</pre>
                    <ol class="docs-list" start="4">
                        <li>Copy <code>target/finance.war</code> into Tomcat 11's <code>webapps/</code> folder, start Tomcat, and open <code>http://localhost:8080/finance/</code>.</li>
                    </ol>
                </section>

                <!-- Limitations -->
                <section id="limitations" class="docs-section">
                    <h2>Known limitations</h2>
                    <ul>
                        <li>Passwords are stored as plain text because the schema's <code>password VARCHAR(16)</code> column is too short for a hash. Widening it to 60 characters and using BCrypt would fix this.</li>
                        <li>The sign-up form lets anyone choose the ADMIN role.</li>
                        <li>Most JSPs print user-entered text with plain <code>${'$'}{...}</code> instead of <code>&lt;c:out&gt;</code>, so it isn't HTML-escaped (XSS risk).</li>
                        <li>Forms have no CSRF tokens.</li>
                        <li>A new database connection is opened for every query; a connection pool would be faster under load.</li>
                    </ul>
                    <p class="docs-note">These limitations come from the fixed schema and the original frontend design.
                        Each one can be fixed without changing the overall architecture.</p>
                </section>

            </article>
        </div>
    </main>

    <footer class="app-footer">
        <p>&copy; 2026 Finance. Online Personal Finance Management System</p>
    </footer>

    <script src="${ctx}/assets/js/main.js"></script>
</body>
</html>
