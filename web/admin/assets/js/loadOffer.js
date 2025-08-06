window.onload = async function () {
  const response = await fetch("../AdminLoadAllOfferData");
};

//closed offer button confirmation using SweetAlert2
document.addEventListener("DOMContentLoaded", function () {
  const editButtons = document.querySelectorAll(".Offer-delete-btn");

  editButtons.forEach((button) => {
    button.addEventListener("click", function (e) {
      e.preventDefault(); // Stop default modal trigger

      Swal.fire({
        title: "Are you sure?",
        text: "Do you want to close this Offer?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Yes, close it!",
      }).then((result) => {
        if (result.isConfirmed) {
          // Now closed the offer manually

          //Then Success
          Swal.fire({
            title: "Closed!",
            text: "Offer closed Successfully",
            icon: "success",
            timer: 1000,
            showConfirmButton: false,
          });
        }
      });
    });
  });
});
