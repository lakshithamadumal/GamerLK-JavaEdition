document.addEventListener("DOMContentLoaded", async function () {

    const response = await fetch("../GetWishlistSession");
    const result = await response.json();

    var notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    if (result.status) {

    } else {
    }

});
