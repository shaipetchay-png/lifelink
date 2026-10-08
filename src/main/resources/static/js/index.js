document.addEventListener("DOMContentLoaded", function () {

    const themeToggle = document.getElementById("themeToggle");

    if (!themeToggle) {
        console.log("Theme toggle button not found.");
        return;
    }

    const icon = themeToggle.querySelector("i");

    // Load saved theme
    const savedTheme = localStorage.getItem("theme");

    if (savedTheme === "dark") {

        document.body.classList.add("dark-mode");

        if (icon) {
            icon.classList.remove("fa-moon");
            icon.classList.add("fa-sun");
        }

    } else {

        document.body.classList.remove("dark-mode");

        if (icon) {
            icon.classList.remove("fa-sun");
            icon.classList.add("fa-moon");
        }
    }


    // Toggle dark mode
    themeToggle.addEventListener("click", function () {

        document.body.classList.toggle("dark-mode");

        const isDark = document.body.classList.contains("dark-mode");

        if (isDark) {

            // Moon → Sun
            if (icon) {
                icon.classList.remove("fa-moon");
                icon.classList.add("fa-sun");
            }

            localStorage.setItem("theme", "dark");

        } else {

            // Sun → Moon
            if (icon) {
                icon.classList.remove("fa-sun");
                icon.classList.add("fa-moon");
            }

            localStorage.setItem("theme", "light");
        }

    });

});

document.addEventListener("DOMContentLoaded", function () {

    const navLinks = document.querySelectorAll(".navbar .nav-link");
    const sections = document.querySelectorAll("#about, #features, #contact");

    function updateActiveNav() {

        const scrollPosition = window.scrollY + 120;
        let currentSection = "";

        sections.forEach(section => {
            const sectionTop = section.offsetTop;
            const sectionBottom = sectionTop + section.offsetHeight;

            if (
                scrollPosition >= sectionTop &&
                scrollPosition < sectionBottom
            ) {
                currentSection = section.id;
            }
        });

        // Kapag nasa pinakababa ng homepage → Contact
        if (
            window.innerHeight + window.scrollY >=
            document.documentElement.scrollHeight - 10
        ) {
            currentSection = "contact";
        }

        navLinks.forEach(link => {
            const linkSection = link.getAttribute("href");

            if (linkSection === "#" + currentSection) {
                link.classList.add("active");
            } else {
                link.classList.remove("active");
            }
        });
    }

    window.addEventListener("scroll", updateActiveNav);

    updateActiveNav();

});