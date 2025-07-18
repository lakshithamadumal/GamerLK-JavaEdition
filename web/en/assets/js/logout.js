async function LogOut() {
    const response = await fetch("../../SignOut");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            window.location.reload();
        } else {
            window.location.reload();
        }
    } else {
        console.log(response);
    }
}

async function LogOutIndex() {
    const response = await fetch("../SignOut");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            window.location.reload();
        } else {
            window.location.reload();
        }
    } else {
        console.log(response);
    }
}