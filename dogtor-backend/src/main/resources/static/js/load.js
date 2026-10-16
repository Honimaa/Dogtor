window.onload = function () {
    document.querySelectorAll("input").forEach((input) => {
      input.value = "";
      input.placeholder = input.getAttribute("placeholder");
    });
  };