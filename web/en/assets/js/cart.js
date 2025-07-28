document.addEventListener("DOMContentLoaded", async function () {
    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    const response = await fetch("../../LoadCartItem");
    if (response.ok) {
        const json = await response.json();
        console.log(json);
    } else {
        notyf.error({
            message: "Cart items loading failed..."
        });
    }


});