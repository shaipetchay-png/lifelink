document.addEventListener("DOMContentLoaded", function () {

    const themeToggle = document.querySelector(".theme-toggle");

    if (themeToggle) {

        const savedTheme = localStorage.getItem("theme");

        if (savedTheme === "dark") {
            document.body.classList.add("dark-mode");
        }

        themeToggle.addEventListener("click", function () {

            document.body.classList.toggle("dark-mode");

            if (document.body.classList.contains("dark-mode")) {
                localStorage.setItem("theme", "dark");
            } else {
                localStorage.setItem("theme", "light");
            }

        });

    }


    // ===========================================
    // SHOW / HIDE PASSWORD
    // ===========================================

    const togglePassword = document.getElementById("toggle-password");
    const password = document.getElementById("password");

    if (togglePassword && password) {

        togglePassword.addEventListener("click", function () {

            if (password.type === "password") {

                password.type = "text";
                togglePassword.textContent = "🙈";
                togglePassword.setAttribute("aria-label", "Hide password");

            } else {

                password.type = "password";
                togglePassword.textContent = "👁";
                togglePassword.setAttribute("aria-label", "Show password");

            }

        });

    }

});