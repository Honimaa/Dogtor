function toggleMenu() {
    var dropdown = document.getElementById("userDropdown");
    dropdown.classList.toggle("show");
  }
  
  // Cerrar el menú si se hace clic fuera
  window.onclick = function(event) {
    if (!event.target.matches('.user-btn')) {
      var dropdowns = document.getElementsByClassName("dropdown-menu");
      for (var i = 0; i < dropdowns.length; i++) {
        var openDropdown = dropdowns[i];
        if (openDropdown.classList.contains('show')) {
          openDropdown.classList.remove('show');
        }
      }
    }
  };
  