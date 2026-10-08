const themeToggle = document.getElementById('themeToggle');
const sidebar = document.getElementById('sidebar');
const mobileMenu = document.getElementById('mobileMenu');
const logoutButton = document.getElementById('logoutButton');

if (localStorage.getItem('lifelink-theme') === 'dark') {
    document.body.classList.add('dark-mode');
}

if (themeToggle) {
    themeToggle.addEventListener('click', () => {
        document.body.classList.toggle('dark-mode');
        localStorage.setItem(
            'lifelink-theme',
            document.body.classList.contains('dark-mode') ? 'dark' : 'light'
        );
    });
}

if (mobileMenu && sidebar) {
    mobileMenu.addEventListener('click', () => {
        sidebar.classList.toggle('open');
    });
}

document.querySelectorAll('.sidebar-item').forEach(item => {
    item.addEventListener('click', () => {
        document.querySelectorAll('.sidebar-item').forEach(link => link.classList.remove('active'));
        item.classList.add('active');
        sidebar?.classList.remove('open');
    });
});

if (logoutButton) {
    logoutButton.addEventListener('click', () => {
        if (confirm('Are you sure you want to logout?')) {
            window.location.href = '/admin/logout';
        }
    });
}
