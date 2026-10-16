document.addEventListener("DOMContentLoaded", function () {
    const dropdown = document.querySelector(".dropdown");
    const menu = document.querySelector(".dropdown-menu");
  
    if (dropdown && menu) {
      dropdown.addEventListener("mouseenter", function () {
        menu.style.display = "block";
      });
  
      dropdown.addEventListener("mouseleave", function () {
        menu.style.display = "none";
      });
    }
  });
  
  function openLoginModal() {
    document.getElementById("loginModal").style.display = "block";
  }

  function closeLoginModal() {
    document.getElementById("loginModal").style.display = "none";
  }

  function openTab(evt, tabName) {
    let tabcontent = document.getElementsByClassName("tabcontent");
    for (let i = 0; i < tabcontent.length; i++) {
      tabcontent[i].style.display = "none";
    }

    let tablinks = document.getElementsByClassName("tablink");
    for (let i = 0; i < tablinks.length; i++) {
      tablinks[i].classList.remove("active");
    }

    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.classList.add("active");
  }

  function redirigirAFormulario() {
    window.location.href = "crear_mascota.html"; // Redirige a la nueva página
  }
  function editarMascota(id) {
    window.location.href = `editar_mascota.html?id=${id}`;
  }
  function redirigirAEditar(id) {
    // Redirige a la página editar_info_usuario.html y pasa el ID como parámetro
    window.location.href = "editar_info_usuario.html?id=" + id;
  }