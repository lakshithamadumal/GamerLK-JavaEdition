async function UpdateProfile() {

    const email = document.getElementById('SettingsEmail').value;
    const newPassword = document.getElementById('SettingsNewPassword').value;
    const conformPassword = document.getElementById('SettingsConformPassword').value;


    const updateBtn = document.getElementById("updateBtn");
    const btnText = document.getElementById("btnText");

    // Disable the button + show spinner
    updateBtn.disabled = true;
    btnText.innerHTML = `<span class="spinner"></span> Updating...`;

    const Updateuser = {
        email,
        newPassword,
        conformPassword
    };

    const response = await fetch("../../UpdateProfile", {
        method: "PUT",
        body: JSON.stringify(Updateuser),
        headers: {
            "Content-type": "application/json"
        }
    });

    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            notyf.success("Password Updated Successfully");
            setTimeout(() => window.location.reload(), 1500);
        }
        else {
            notyf.error(json.message);
            resetButton();
        }
    }
    else {
        notyf.error("Profile Update failed. Please try again later.");
        resetButton();
    }

    function resetButton() {
        updateBtn.disabled = false;
        btnText.innerHTML = "Update Profile";
    }

}
