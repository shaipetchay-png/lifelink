/* ==========================================
   LIFE LINK - DONOR INFORMATION
========================================== */


/* =========================
   DARK MODE
========================= */

const themeToggle =
    document.getElementById("themeToggle");


const savedTheme =
    localStorage.getItem("lifelink-theme");


if (savedTheme === "dark") {

    document.body.classList.add("dark-mode");

}


if (themeToggle) {

    themeToggle.addEventListener(
        "click",
        function () {

            document.body.classList.toggle(
                "dark-mode"
            );


            if (
                document.body.classList.contains(
                    "dark-mode"
                )
            ) {

                localStorage.setItem(
                    "lifelink-theme",
                    "dark"
                );

            } else {

                localStorage.setItem(
                    "lifelink-theme",
                    "light"
                );

            }

        }
    );

}


/* =========================
   MOBILE SIDEBAR
========================= */

const mobileMenu =
    document.getElementById("mobileMenu");


const sidebar =
    document.getElementById("sidebar");


if (mobileMenu && sidebar) {

    mobileMenu.addEventListener(
        "click",
        function () {

            sidebar.classList.toggle("open");

        }
    );

}


/* =========================
   NOTIFICATION
========================= */

const notificationButton =
    document.getElementById(
        "notificationButton"
    );


if (notificationButton) {

    notificationButton.addEventListener(
        "click",
        function () {

            alert(
                "You currently have no new notifications."
            );

        }
    );

}


/* =========================
   LOGOUT
========================= */

const logoutButton =
    document.getElementById(
        "logoutButton"
    );


if (logoutButton) {

    logoutButton.addEventListener(
        "click",
        function () {

            const confirmLogout =
                confirm(
                    "Are you sure you want to logout?"
                );


            if (confirmLogout) {

                window.location.href =
                    "/login";

            }

        }
    );

}