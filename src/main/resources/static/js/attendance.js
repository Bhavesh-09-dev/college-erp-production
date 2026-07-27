/* ============================================================
   Attendance Marking - Bulk Operations
   ============================================================ */

function markAllPresent() {
    document.querySelectorAll('.attendance-status-select').forEach(function (select) {
        select.value = 'PRESENT';
    });
    updateAttendanceCounts();
}

function markAllAbsent() {
    document.querySelectorAll('.attendance-status-select').forEach(function (select) {
        select.value = 'ABSENT';
    });
    updateAttendanceCounts();
}

function updateAttendanceCounts() {
    let present = 0, absent = 0, late = 0, excused = 0;
    document.querySelectorAll('.attendance-status-select').forEach(function (select) {
        switch (select.value) {
            case 'PRESENT': present++; break;
            case 'ABSENT': absent++; break;
            case 'LATE': late++; break;
            case 'EXCUSED': excused++; break;
        }
    });

    const presentEl = document.getElementById('presentCount');
    const absentEl = document.getElementById('absentCount');
    const lateEl = document.getElementById('lateCount');
    const excusedEl = document.getElementById('excusedCount');

    if (presentEl) presentEl.textContent = present;
    if (absentEl) absentEl.textContent = absent;
    if (lateEl) lateEl.textContent = late;
    if (excusedEl) excusedEl.textContent = excused;
}

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.attendance-status-select').forEach(function (select) {
        select.addEventListener('change', updateAttendanceCounts);
    });
    updateAttendanceCounts();
});
