// Lógica para Testimonios
class Testimonial {
  constructor(text, name, image, rating, service, date) {
    this.text = this.formatText(text);
    this.name = name;
    this.image = image || "https://via.placeholder.com/60";
    this.rating = "⭐".repeat(rating);
    this.service = service;
    this.date = date || new Date().toLocaleDateString();
  }

  formatText(text) {
    return text.replace(/(\S{15,})/g, "$1- ");
  }
}

const testimonials = [
  new Testimonial(
    "¡Excelente servicio, muy atentos con mi mascota!",
    "Peter Parker",
    "https://randomuser.me/api/portraits/men/10.jpg",
    5,
    "Consulta General",
    "15 de enero de 2024"
  ),
  new Testimonial(
    "¡La cirugía salió perfectamente y mi perro se recuperó rápido!",
    "Walter White",
    "https://randomuser.me/api/portraits/men/20.jpg",
    4,
    "Cirugía",
    "10 de febrero de 2024"
  ),
  new Testimonial(
    "¡Increíble servicio de peluquería! Mi gato luce fabuloso.",
    "Mary Jane",
    "https://randomuser.me/api/portraits/women/30.jpg",
    3,
    "Peluquería",
    "5 de marzo de 2024"
  ),
];

function rebuildTestimonials() {
  const testimonialsContainer = document.querySelector(".testimonial-grid");
  testimonialsContainer.innerHTML = "";

  testimonials.forEach((testimonial) => {
    const testimonialCard = document.createElement("div");
    testimonialCard.className = "testimonial";
    testimonialCard.innerHTML = `
      <div class="testimonial-content">
        <img src="${testimonial.image}" alt="Cliente" />
        <div class="testimonial-text">
          <p><strong>"${testimonial.text}</strong>"</p>
          <p>- ${testimonial.name}</p>
          <p class="stars">${testimonial.rating}</p>
          <p class="testimonial-date">${testimonial.date}</p>
          <p class="testimonial-service">Servicio: <strong>${testimonial.service}</strong></p>
        </div>
      </div>
    `;
    testimonialsContainer.appendChild(testimonialCard);
  });
}

// Lógica para Agendar Cita
document.addEventListener("DOMContentLoaded", () => {
  rebuildTestimonials();

  const vetOptions = document.querySelectorAll(".vet-option");
  const selectedVetInput = document.getElementById("selected-vet");
  const appointmentBtn = document.getElementById("appointment-btn");

  // Función para verificar la validez del formulario de agendar cita
  function checkAppointmentFormValidity() {
    const name = document.getElementById("appointment-name").value;
    const details = document.getElementById("appointment-details").value;
    const service = document.getElementById("appointment-service").value;
    const image = document.getElementById("appointment-image").value;
    const vet = selectedVetInput.value;

    // Habilitar el botón solo si todos los campos están completos
    if (name && details && service && image && vet) {
      appointmentBtn.disabled = false;
    } else {
      appointmentBtn.disabled = true;
    }
  }

  // Eventos para los campos del formulario de agendar cita
  document.querySelectorAll("#appointment-form input, #appointment-form textarea, #appointment-form select").forEach((input) => {
    input.addEventListener("input", checkAppointmentFormValidity);
  });

  // Evento para evitar que el formulario se desplace al seleccionar un servicio
  document.getElementById("appointment-service").addEventListener("change", (e) => {
    e.preventDefault();
    checkAppointmentFormValidity();
  });

  // Eventos para las opciones de veterinaria
  vetOptions.forEach((vet) => {
    vet.addEventListener("click", () => {
      vetOptions.forEach((v) => {
        v.classList.remove("selected");
        v.style.background = "rgba(255, 255, 255, 0.7)";
        v.style.transform = "scale(1)";
      });

      vet.classList.add("selected");
      vet.style.background = "#3c5b6f";
      vet.style.transform = "scale(1.3)";

      selectedVetInput.value = vet.getAttribute("data-vet");
      checkAppointmentFormValidity();
    });
  });

  // Evitar que el formulario se envíe si no se selecciona una veterinaria
  document.getElementById("appointment-form").addEventListener("submit", function (e) {
    if (!selectedVetInput.value) {
      e.preventDefault();
      alert("Debes seleccionar una veterinaria.");
    }
  });

  // Verificar la validez del formulario al cargar la página
  checkAppointmentFormValidity();
});

document.addEventListener("DOMContentLoaded", function () {
  const track = document.querySelector(".carousel-track");
  const cards = document.querySelectorAll(".foundation-card");

  let speed = 0.5;
  let position = 0;

  cards.forEach((card) => {
    let clone = card.cloneNode(true);
    track.appendChild(clone);
  });

  function animate() {
    position -= speed;
    track.style.transform = `translateX(${position}px)`;

    if (position <= -track.scrollWidth / 2) {
      position = 0;
    }

    requestAnimationFrame(animate);
  }

  animate();
});

function toggleMenu() {
  document.querySelector(".user-menu").classList.toggle("active");
}

// Cierra el menú si se hace clic fuera de él
document.addEventListener("click", function(event) {
  const userMenu = document.querySelector(".user-menu");
  if (!userMenu.contains(event.target)) {
    userMenu.classList.remove("active");
  }
});