<%-- 
    Document   : activityTimeline
    Created on : Feb 3, 2026, 4:34:05 PM
    Author     : Viehai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Customer Activity - Nguyễn Văn A</title>
        <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity-timeline.css">
    </head>
    <body>
        <div class="container">
            <!-- Browser Header -->
            <div class="header"></div>

            <!-- Main Page -->
            <div class="page-wrapper">
                <!-- Customer Header -->
                <div class="customer-header">
                    <h1 class="customer-title">Customer Activity: Nguyễn Văn A (#123)</h1>

                    <div class="customer-info-grid">
                        <div class="info-item">
                            <span class="info-label">Company:</span>
                            <span class="info-value">Công ty ABC</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Email:</span>
                            <span class="info-value">a@abc.com</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Phone:</span>
                            <span class="info-value">0910xxxxxx</span>
                        </div>
                    </div>

                    <div class="info-item" style="margin-top: 12px;">
                        <span class="info-label">Address:</span>
                        <span class="info-value">123 Nguyễn Huệ, Phường Bến Nghé, Quận 1, TPHCM</span>
                    </div>
                </div>

                <!-- Activities Section -->
                <div class="activities-section">
                    <div class="section-header">
                        <h2 class="section-title">Activities Timeline</h2>
                        <button class="btn btn-primary" onclick="window.location.href = 'create-edit-activity.html'">
                            <span>New Activity</span>
                        </button>
                    </div>

                    <!-- Timeline Table -->
                    <table class="timeline-table">
                        <thead>
                            <tr>
                                <th>Time</th>
                                <th>Type</th>
                                <th>Title</th>
                                <th>Description</th>
                                <th>Assigned To</th>
                                <th>Status</th>
                                <th>Creator</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td class="time-cell">
                                    <span class="time-date">2026-01-12</span>
                                    <span class="time-hour">15:00</span>
                                </td>
                                <td>
                                    <div class="type-cell">
                                        <div class="type-icon">📞</div>
                                        <div class="type-label">Phone Call</div>
                                    </div>
                                </td>
                                <td class="title-cell">Follow-up on proposal</td>
                                <td class="description-cell">
                                    Discussed terms and next steps. Customer requested a revised quote by end of week.
                                </td>
                                <td class="assigned-cell">Me</td>
                                <td>
                                    <span class="status-badge status-completed">Completed</span>
                                </td>
                                <td class="creator-cell">Me</td>
                                <td>
                                    <div class="actions-cell">
                                        <a href="create-edit-activity.html?id=1" class="action-link">Edit</a>
                                        <span class="action-separator">|</span>
                                        <a href="#" class="action-link" onclick="deleteActivity(1); return false;">Delete</a>
                                    </div>
                                </td>
                            </tr>

                            <tr>
                                <td class="time-cell">
                                    <span class="time-date">2026-01-10</span>
                                    <span class="time-hour">10:00</span>
                                </td>
                                <td>
                                    <div class="type-cell">
                                        <div class="type-icon">🎥</div>
                                        <div class="type-label">Meeting</div>
                                    </div>
                                </td>
                                <td class="title-cell">Initial client meeting</td>
                                <td class="description-cell">
                                    Introduced services and gathered requirements. Scheduled a follow-up for proposal presentation.
                                </td>
                                <td class="assigned-cell">Me</td>
                                <td>
                                    <span class="status-badge status-in-progress">In Progress</span>
                                </td>
                                <td class="creator-cell">Manager</td>
                                <td>
                                    <div class="actions-cell">
                                        <a href="create-edit-activity.html?id=2" class="action-link">Edit</a>
                                        <span class="action-separator">|</span>
                                        <a href="#" class="action-link" onclick="deleteActivity(2); return false;">Delete</a>
                                    </div>
                                </td>
                            </tr>

                            <tr>
                                <td class="time-cell">
                                    <span class="time-date">2026-01-08</span>
                                    <span class="time-hour">14:30</span>
                                </td>
                                <td>
                                    <div class="type-cell">
                                        <div class="type-icon">✉️</div>
                                        <div class="type-label">Email</div>
                                    </div>
                                </td>
                                <td class="title-cell">Sent product catalog</td>
                                <td class="description-cell">
                                    Emailed comprehensive product catalog with pricing. Customer expressed interest in premium package.
                                </td>
                                <td class="assigned-cell">Me</td>
                                <td>
                                    <span class="status-badge status-completed">Completed</span>
                                </td>
                                <td class="creator-cell">Me</td>
                                <td>
                                    <div class="actions-cell">
                                        <a href="create-edit-activity.html?id=3" class="action-link">Edit</a>
                                        <span class="action-separator">|</span>
                                        <a href="#" class="action-link" onclick="deleteActivity(3); return false;">Delete</a>
                                    </div>
                                </td>
                            </tr>

                            <tr>
                                <td class="time-cell">
                                    <span class="time-date">2026-01-05</span>
                                    <span class="time-hour">11:00</span>
                                </td>
                                <td>
                                    <div class="type-cell">
                                        <div class="type-icon">✓</div>
                                        <div class="type-label">Task</div>
                                    </div>
                                </td>
                                <td class="title-cell">Research competitor pricing</td>
                                <td class="description-cell">
                                    Analyzed market competitors to prepare competitive proposal. Found opportunities to highlight our value proposition.
                                </td>
                                <td class="assigned-cell">Sales Team</td>
                                <td>
                                    <span class="status-badge status-completed">Completed</span>
                                </td>
                                <td class="creator-cell">Manager</td>
                                <td>
                                    <div class="actions-cell">
                                        <a href="create-edit-activity.html?id=4" class="action-link">Edit</a>
                                        <span class="action-separator">|</span>
                                        <a href="#" class="action-link" onclick="deleteActivity(4); return false;">Delete</a>
                                    </div>
                                </td>
                            </tr>

                            <tr>
                                <td class="time-cell">
                                    <span class="time-date">2026-01-03</span>
                                    <span class="time-hour">09:15</span>
                                </td>
                                <td>
                                    <div class="type-cell">
                                        <div class="type-icon">📝</div>
                                        <div class="type-label">Note</div>
                                    </div>
                                </td>
                                <td class="title-cell">First contact made</td>
                                <td class="description-cell">
                                    Received inquiry via website form. Customer looking for enterprise solutions. High potential deal.
                                </td>
                                <td class="assigned-cell">Me</td>
                                <td>
                                    <span class="status-badge status-completed">Completed</span>
                                </td>
                                <td class="creator-cell">Me</td>
                                <td>
                                    <div class="actions-cell">
                                        <a href="create-edit-activity.html?id=5" class="action-link">Edit</a>
                                        <span class="action-separator">|</span>
                                        <a href="#" class="action-link" onclick="deleteActivity(5); return false;">Delete</a>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Back Button -->
            <button class="back-button" onclick="window.location.href = 'activity-dashboard.html'">
                <span>←</span>
                <span>Back to Dashboard</span>
            </button>
        </div>
        <script src="${pageContext.request.contextPath}/assets/js/activity-timeline.js"></script>
    </body>
</html>
