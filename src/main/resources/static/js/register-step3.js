// Show / Hide Password
const togglePassword = document.getElementById("togglePassword");
const password = document.getElementById("password");

togglePassword.addEventListener("click", function () {

    if (password.type === "password") {

        password.type = "text";
        togglePassword.innerHTML = '<i class="fa-solid fa-eye-slash"></i>';

    } else {

        password.type = "password";
        togglePassword.innerHTML = '<i class="fa-solid fa-eye"></i>';

    }

});

// Next Button Validation
document.getElementById("nextBtn").addEventListener("click", function (e) {

    e.preventDefault();

    const username = document.getElementById("username").value.trim();
    const pass = document.getElementById("password").value;
    const confirm = document.getElementById("confirmPassword").value;

    if (username === "" || pass === "" || confirm === "") {

        alert("Please fill in all fields.");

        return;

    }

    if (pass !== confirm) {

        alert("Passwords do not match.");

        return;

    }

    window.location.href = "register-review.html";

});