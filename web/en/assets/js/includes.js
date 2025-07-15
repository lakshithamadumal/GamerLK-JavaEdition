// Fetching the footer dynamically
fetch('../includes/footer.html')
    .then(res => res.text())
    .then(data => {
        document.getElementById('footer').innerHTML = data;
    });

// Fetching the newsletter dynamically
fetch('../includes/newsletter.html')
    .then(res => res.text())
    .then(data => {
        document.getElementById('newsletter').innerHTML = data;
    });    