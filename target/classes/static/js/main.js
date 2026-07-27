/* ============================================================
   Smart College ERP Portal - Main JavaScript
   ============================================================ */

// Toggle sidebar on mobile
document.addEventListener('DOMContentLoaded', function () {
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebar = document.querySelector('.sidebar');

    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function () {
            sidebar.classList.toggle('show');
        });
    }

    // Auto-dismiss alerts after 5 seconds
    document.querySelectorAll('.alert-dismissible').forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            bsAlert.close();
        }, 5000);
    });

    // Initialize tooltips
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    [...tooltipTriggerList].forEach(el => new bootstrap.Tooltip(el));

    // Confirm delete actions
    document.querySelectorAll('.confirm-delete').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            const message = form.dataset.confirmMessage || 'Are you sure you want to delete this item?';
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });

    // Live search debounce
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        let timeout;
        searchInput.addEventListener('input', function () {
            clearTimeout(timeout);
            timeout = setTimeout(() => {
                searchInput.closest('form').submit();
            }, 600);
        });
    }
});

// Set attendance progress bar colors based on percentage
function setAttendanceBarColor(element, percentage) {
    element.classList.remove('low', 'medium', 'high');
    if (percentage < 65) {
        element.classList.add('low');
    } else if (percentage < 75) {
        element.classList.add('medium');
    } else {
        element.classList.add('high');
    }
}

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('[data-attendance-bar]').forEach(function (bar) {
        const pct = parseFloat(bar.dataset.attendanceBar);
        setAttendanceBarColor(bar, pct);
    });
});

// Toggle password visibility
function togglePassword(inputId, iconId) {
    const input = document.getElementById(inputId);
    const icon = document.getElementById(iconId);
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.replace('bi-eye', 'bi-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.replace('bi-eye-slash', 'bi-eye');
    }
}
