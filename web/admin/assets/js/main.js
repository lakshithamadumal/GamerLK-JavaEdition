//Update the category modal trigger to use SweetAlert2 for confirmation
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".category-edit-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to update this category?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, update it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now open the modal manually
                    const updateModal = new bootstrap.Modal(document.getElementById("updateCategoryModal"));
                    updateModal.show();

                    //Then Success
                    Swal.fire({
                        title: "Updated!",
                        text: "Category Updated Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });
                }
            });
        });
    });
});


//Delete button confirmation using SweetAlert2
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".category-delete-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to delete this category?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, delete it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now delete the category manually

                    //Then Success
                    Swal.fire({
                        title: "Deleted!",
                        text: "Category Deleted Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });

                }
            });
        });
    });
});




//Update the game modal trigger to use SweetAlert2 for confirmation
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".game-edit-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to update this game?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, update it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now open the modal manually
                    window.location.href = "General-Add-Game.html";

                    //Then Success
                    Swal.fire({
                        title: "Updated!",
                        text: "Game Updated Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });
                }
            });
        });
    });
});




//Update the Developer modal trigger to use SweetAlert2 for confirmation
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".Developer-edit-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to update this developer?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, update it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now open the modal manually
                    const updateModal = new bootstrap.Modal(document.getElementById("updateDeveloperModal"));
                    updateModal.show();

                    //Then Success
                    Swal.fire({
                        title: "Updated!",
                        text: "Developer Updated Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });
                }
            });
        });
    });
});


//Delete button confirmation using SweetAlert2
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".Developer-delete-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to delete this Developer?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, delete it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now delete the category manually

                    //Then Success
                    Swal.fire({
                        title: "Deleted!",
                        text: "Developer Deleted Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });

                }
            });
        });
    });
});


//Update the offer modal trigger to use SweetAlert2 for confirmation
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".Offer-edit-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to update this offer?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, update it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now open the modal manually
                    const updateModal = new bootstrap.Modal(document.getElementById("updateOfferModal"));
                    updateModal.show();

                    //Then Success
                    Swal.fire({
                        title: "Updated!",
                        text: "Offer Updated Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });
                }
            });
        });
    });
});


//closed offer button confirmation using SweetAlert2
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".Offer-delete-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to close this Offer?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, close it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now closed the offer manually

                    //Then Success
                    Swal.fire({
                        title: "Closed!",
                        text: "Offer closed Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });

                }
            });
        });
    });
});

//unban Customer button confirmation using SweetAlert2
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".unban-customer-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to unban this Customer?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, unban it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now closed the offer manually

                    //Then Success
                    Swal.fire({
                        title: "Unbaned!",
                        text: "Customer unbaned Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });

                }
            });
        });
    });
});

//ban Customer button confirmation using SweetAlert2
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".ban-customer-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to ban this Customer?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, ban it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now closed the offer manually

                    //Then Success
                    Swal.fire({
                        title: "Baned!",
                        text: "Customer baned Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });

                }
            });
        });
    });
});


//Remove Subscriber button confirmation using SweetAlert2
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".remove-Subscriber-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to remove this Subscriber?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, remove it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now closed the offer manually

                    //Then Success
                    Swal.fire({
                        title: "Removed!",
                        text: "Subscriber removed Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });

                }
            });
        });
    });
});


//inquiry-solved-btn button confirmation using SweetAlert2
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".inquiry-solved-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Have you resolved the issue?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, resolved it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now closed the offer manually

                    //Then Success
                    Swal.fire({
                        title: "Done!",
                        text: "Marked as resolved successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });

                }
            });
        });
    });
});


//Remove requirement button confirmation using SweetAlert2
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".requirement-delete-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to remove this Requirement?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, remove it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now closed the offer manually

                    //Then Success
                    Swal.fire({
                        title: "Removed!",
                        text: "Requirement removed Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });

                }
            });
        });
    });
});



//Update the Requirement modal trigger to use SweetAlert2 for confirmation
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".requirement-edit-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to update this Requirement?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, update it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now open the modal manually
                    const updateModal = new bootstrap.Modal(document.getElementById("updateRequirementModal"));
                    updateModal.show();

                    //Then Success
                    Swal.fire({
                        title: "Updated!",
                        text: "Requirement Updated Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });
                }
            });
        });
    });
});



//Remove Mode button confirmation using SweetAlert2
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".mode-delete-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to remove this Game Mode?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, remove it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now closed the offer manually

                    //Then Success
                    Swal.fire({
                        title: "Removed!",
                        text: "Game Mode removed Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });

                }
            });
        });
    });
});


//Update the Game Mode modal trigger to use SweetAlert2 for confirmation
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".mode-edit-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to update this Game Mode?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, update it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now open the modal manually
                    const updateModal = new bootstrap.Modal(document.getElementById("updateModeModal"));
                    updateModal.show();

                    //Then Success
                    Swal.fire({
                        title: "Updated!",
                        text: "Game Mode Updated Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });
                }
            });
        });
    });
});