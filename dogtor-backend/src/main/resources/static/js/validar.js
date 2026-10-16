function validarContraseña() {
    let password = document.getElementById("contrasena").value;
    let confirmPassword = document.getElementById("confirm_password").value;

    if (password.length > 0 && password !== confirmPassword) {
        alert("Las contraseñas no coinciden.");
        return false;
    }
    return true;
}