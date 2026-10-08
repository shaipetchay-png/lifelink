/* =========================================================
   LIFELINK - UNIFIED THEME TOGGLE
   One behavior for every page. Uses the existing
   lifelink-theme key so the user's preference is preserved.
========================================================= */
(function () {
    const STORAGE_KEY = "lifelink-theme";

    function applyTheme(theme) {
        document.body.classList.toggle("dark-mode", theme === "dark");
        document.querySelectorAll(".theme-toggle").forEach(function (button) {
            button.setAttribute("aria-pressed", theme === "dark" ? "true" : "false");
            button.setAttribute("aria-label", theme === "dark" ? "Switch to light mode" : "Switch to dark mode");
            button.setAttribute("title", theme === "dark" ? "Switch to light mode" : "Switch to dark mode");
        });
    }

    const saved = localStorage.getItem(STORAGE_KEY);
    applyTheme(saved === "dark" ? "dark" : "light");

    document.querySelectorAll(".theme-toggle").forEach(function (button) {
        button.addEventListener("click", function (event) {
            // Prevent older page-specific toggle handlers from running too.
            event.preventDefault();
            event.stopImmediatePropagation();

            const next = document.body.classList.contains("dark-mode") ? "light" : "dark";
            localStorage.setItem(STORAGE_KEY, next);
            applyTheme(next);
        }, true);
    });
})();
