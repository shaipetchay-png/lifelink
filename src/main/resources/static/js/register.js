document.addEventListener("DOMContentLoaded", function () {

    /* =========================================================
       REGISTRATION FORM
    ========================================================= */

    const registrationForm =
        document.getElementById("registrationForm");


    if (registrationForm) {

        registrationForm.addEventListener(
            "submit",
            function (event) {

                const password =
                    document.getElementById(
                        "password"
                    );

                const confirmPassword =
                    document.getElementById(
                        "confirmPassword"
                    );

                const termsCheckbox =
                    document.getElementById(
                        "termsCheckbox"
                    );


                // ==============================
                // PASSWORD CHECK
                // ==============================

                if (
                    password &&
                    confirmPassword &&
                    password.value !==
                    confirmPassword.value
                ) {

                    event.preventDefault();

                    alert(
                        "Passwords do not match."
                    );

                    confirmPassword.focus();

                    return;

                }


                // ==============================
                // TERMS CHECK
                // ==============================

                if (
                    termsCheckbox &&
                    !termsCheckbox.checked
                ) {

                    event.preventDefault();

                    alert(
                        "Please agree to the Terms and Conditions and Privacy Policy."
                    );

                    return;

                }


                /*
                 * IMPORTANT:
                 *
                 * Kapag valid lahat,
                 * HUWAG nating gamitin ang
                 * event.preventDefault().
                 *
                 * Automatic na ipapadala ng browser
                 * ang form sa:
                 *
                 * POST /register
                 *
                 * at si RegisterController
                 * ang magse-save sa MySQL.
                 */

            }
        );

    }


    /* =========================================================
       TERMS AND CONDITIONS MODAL
    ========================================================= */

    window.openTermsModal =
        function (event) {

            event.preventDefault();

            const modal =
                document.getElementById(
                    "termsModal"
                );


            if (modal) {

                modal.classList.add(
                    "active"
                );

                document.body.style.overflow =
                    "hidden";

            }

        };


    window.closeTermsModal =
        function () {

            const modal =
                document.getElementById(
                    "termsModal"
                );


            if (modal) {

                modal.classList.remove(
                    "active"
                );

                document.body.style.overflow =
                    "";

            }

        };


    /* =========================================================
       PRIVACY POLICY MODAL
    ========================================================= */

    window.openPrivacyModal =
        function (event) {

            event.preventDefault();

            const modal =
                document.getElementById(
                    "privacyModal"
                );


            if (modal) {

                modal.classList.add(
                    "active"
                );

                document.body.style.overflow =
                    "hidden";

            }

        };


    window.closePrivacyModal =
        function () {

            const modal =
                document.getElementById(
                    "privacyModal"
                );


            if (modal) {

                modal.classList.remove(
                    "active"
                );

                document.body.style.overflow =
                    "";

            }

        };


    /* =========================================================
       CLOSE MODALS WHEN CLICKING OUTSIDE
    ========================================================= */

    window.addEventListener(
        "click",
        function (event) {

            const termsModal =
                document.getElementById(
                    "termsModal"
                );

            const privacyModal =
                document.getElementById(
                    "privacyModal"
                );


            if (
                termsModal &&
                event.target === termsModal
            ) {

                closeTermsModal();

            }


            if (
                privacyModal &&
                event.target === privacyModal
            ) {

                closePrivacyModal();

            }

        }
    );


    /* =========================================================
       CLOSE MODALS WITH ESC
    ========================================================= */

    document.addEventListener(
        "keydown",
        function (event) {

            if (event.key === "Escape") {

                closeTermsModal();

                closePrivacyModal();

            }

        }
    );

});