const STAR_COUNT = 38;
const STAR_MIN_SIZE = 1;
const STAR_MAX_SIZE = 2.7;
const STAR_COLOR = "rgba(255,255,255,0.40)";

function randomBetween(min, max) {
  return Math.random() * (max - min) + min;
}

function createStars() {
  const starfield = document.getElementById("starfield");
  if (!starfield) return;
  starfield.innerHTML = "";
  const { innerWidth: w, innerHeight: h } = window;
  for (let i = 0; i < STAR_COUNT; i++) {
    const d = document.createElement("div");
    d.className = "star-dot";
    let size = randomBetween(STAR_MIN_SIZE, STAR_MAX_SIZE);
    d.style.width = `${size}px`;
    d.style.height = `${size}px`;
    d.style.position = "absolute";
    d.style.left = `${randomBetween(5, w - 5)}px`;
    d.style.top = `${randomBetween(10, h - 10)}px`;
    d.style.background = STAR_COLOR;
    d.style.borderRadius = "50%";
    d.style.boxShadow = `0 0 ${randomBetween(2, 8)}px 1.5px #fff2`;
    d.style.pointerEvents = "none";
    d.style.opacity = randomBetween(0.7, 1).toFixed(2);
    starfield.appendChild(d);
  }
}
window.addEventListener("resize", createStars);
window.addEventListener("DOMContentLoaded", createStars);
