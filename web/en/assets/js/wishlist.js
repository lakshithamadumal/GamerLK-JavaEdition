document.addEventListener("DOMContentLoaded", async function () {
    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    const response = await fetch("../../LoadWishlistItem");
    if (response.ok) {
        const json = await response.json();
        console.log(json);
    } else {
        notyf.error({
            message: "Wishlist items loading failed..."
        });
    }


});