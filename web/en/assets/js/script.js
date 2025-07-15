// (Optional) Carousel dot indicator animation - demo logic. No slider implemented, just toggles on click.
document.addEventListener("DOMContentLoaded", function () {
  const dots = document.querySelectorAll('.carousel-pagination .dot');
  if (dots.length) {
    dots.forEach((dot, idx) => {
      dot.addEventListener('click', () => {
        dots.forEach(d => d.classList.remove('active'));
        dot.classList.add('active');
      });
    });
  }
  // Mobile sidebar toggle placeholder
  document.querySelectorAll('.sidebar-toggle').forEach(btn => {
    btn.addEventListener('click', function () {
      document.querySelector('.sidebar').classList.toggle('active');
    });
  });
});

//cursor
document.addEventListener('DOMContentLoaded', function () {
  const cursor = document.querySelector('.cursor');
  const follower = document.querySelector('.cursor-follower');
  const elements = document.querySelectorAll('*'); // Select all elements

  let posX = 0, posY = 0;
  let mouseX = 0, mouseY = 0;

  // Mouse move event
  document.addEventListener('mousemove', function (e) {
    mouseX = e.clientX;
    mouseY = e.clientY;
  });

  // Animation loop
  function updateCursor() {
    // Main cursor (dot)
    posX += (mouseX - posX) / 9;
    posY += (mouseY - posY) / 9;

    cursor.style.left = mouseX + 'px';
    cursor.style.top = mouseY + 'px';

    // Follower cursor (circle)
    follower.style.left = posX + 'px';
    follower.style.top = posY + 'px';

    requestAnimationFrame(updateCursor);
  }

  updateCursor();

  // Hover effects
  elements.forEach(element => {
    element.addEventListener('mouseenter', () => {
      cursor.classList.add('active');
      follower.classList.add('active');
    });

    element.addEventListener('mouseleave', () => {
      cursor.classList.remove('active');
      follower.classList.remove('active');
    });
  });
});

document.addEventListener("DOMContentLoaded", function () {
  const carousel = document.querySelector('.carousel-banner');
  const images = document.querySelectorAll('.carousel-img');
  const dots = document.querySelectorAll('.carousel-pagination .dot');
  let currentIndex = 0;
  const totalImages = images.length;
  let autoSlideInterval;

  // Initialize carousel
  function initCarousel() {
    images[0].classList.add('active');
    dots[0].classList.add('active');
  }

  // Update carousel position
  function updateCarousel(index) {
    // Remove active class from all images and dots
    images.forEach(img => img.classList.remove('active'));
    dots.forEach(dot => dot.classList.remove('active'));

    // Add active class to current image and dot
    images[index].classList.add('active');
    dots[index].classList.add('active');

    currentIndex = index;
  }

  // Auto-animate the carousel
  function startAutoSlide() {
    return setInterval(() => {
      currentIndex = (currentIndex + 1) % totalImages;
      updateCarousel(currentIndex);
    }, 3000);
  }

  // Handle dot click
  dots.forEach((dot, index) => {
    dot.addEventListener('click', () => {
      clearInterval(autoSlideInterval);
      updateCarousel(index);
      autoSlideInterval = startAutoSlide();
    });
  });

  // Initialize and start the carousel
  initCarousel();
  autoSlideInterval = startAutoSlide();
});


// Horizontal drag scrolling with auto-animate for developer cards
document.addEventListener("DOMContentLoaded", function () {
  const devListContainer = document.querySelector('.dev-list-container');
  const devList = document.querySelector('.dev-list');

  if (devListContainer && devList) {
    // Drag scroll functionality
    let isDown = false;
    let isDragging = false;
    let startX;
    let scrollLeft;
    let animationId;
    let scrollSpeed = 1; // pixels per frame (adjust for speed)
    let direction = 1; // 1 for right, -1 for left
    let isPaused = false;

    // Clone cards for infinite loop effect
    const cards = devList.querySelectorAll('.dev-card');
    cards.forEach(card => {
      devList.appendChild(card.cloneNode(true));
    });

    // Auto-scroll function
    function autoScroll() {
      if (!isPaused && !isDragging) {
        const maxScroll = devList.scrollWidth - devListContainer.clientWidth;

        if (devListContainer.scrollLeft >= maxScroll - 1) {
          // When reaching end, reset to start for infinite effect
          devListContainer.scrollLeft = 0;
        } else if (devListContainer.scrollLeft <= 0) {
          // When at start, scroll right
          direction = 1;
        }

        devListContainer.scrollLeft += scrollSpeed * direction;
      }
      animationId = requestAnimationFrame(autoScroll);
    }

    // Start auto-scroll
    autoScroll();

    // Pause on hover
    devListContainer.addEventListener('mouseenter', () => {
      isPaused = true;
    });

    devListContainer.addEventListener('mouseleave', () => {
      isPaused = false;
    });

    // Drag events
    devListContainer.addEventListener('mousedown', (e) => {
      isDown = true;
      isDragging = true;
      startX = e.pageX - devListContainer.offsetLeft;
      scrollLeft = devListContainer.scrollLeft;
      devListContainer.style.cursor = 'grabbing';
      devListContainer.style.scrollBehavior = 'auto';
    });

    devListContainer.addEventListener('mouseleave', () => {
      isDown = false;
      isDragging = false;
      devListContainer.style.cursor = 'grab';
      devListContainer.style.scrollBehavior = 'smooth';
    });

    devListContainer.addEventListener('mouseup', () => {
      isDown = false;
      isDragging = false;
      devListContainer.style.cursor = 'grab';
      devListContainer.style.scrollBehavior = 'smooth';
      // Reset auto-scroll direction based on last drag direction
      direction = (devListContainer.scrollLeft > scrollLeft) ? 1 : -1;
    });

    devListContainer.addEventListener('mousemove', (e) => {
      if (!isDown) return;
      e.preventDefault();
      const x = e.pageX - devListContainer.offsetLeft;
      const walk = (x - startX) * 2;
      devListContainer.scrollLeft = scrollLeft - walk;
    });

    // Touch events for mobile
    devListContainer.addEventListener('touchstart', (e) => {
      isDown = true;
      isDragging = true;
      startX = e.touches[0].pageX - devListContainer.offsetLeft;
      scrollLeft = devListContainer.scrollLeft;
      cancelAnimationFrame(animationId);
    });

    devListContainer.addEventListener('touchend', () => {
      isDown = false;
      isDragging = false;
      autoScroll(); // Restart animation
    });

    devListContainer.addEventListener('touchmove', (e) => {
      if (!isDown) return;
      const x = e.touches[0].pageX - devListContainer.offsetLeft;
      const walk = (x - startX) * 2;
      devListContainer.scrollLeft = scrollLeft - walk;
    });

    // Smooth scroll on wheel event
    devListContainer.addEventListener('wheel', (e) => {
      e.preventDefault();
      devListContainer.scrollLeft += e.deltaY * 0.5;
    }, { passive: false });

    // Clean up animation frame when leaving page
    window.addEventListener('beforeunload', () => {
      cancelAnimationFrame(animationId);
    });
  }
});







// Scroll Animation Functionality
document.addEventListener("DOMContentLoaded", function () {
  // Add scroll-animate class to all sections
  const sections = document.querySelectorAll('.special-offers, .game-se, .category-se, .dev-se, .single-game-banner, .SingleView-detail, .SingleView-detail-dev, .SingleView-detail, footer, .newsleter');
  sections.forEach((section, index) => {
    section.classList.add('scroll-animate');
    // Add staggered delays
    section.classList.add(`delay-${index % 4}`);
  });

  // Intersection Observer for scroll animations
  const observerOptions = {
    threshold: 0.15, // Trigger when 15% of the section is visible
    rootMargin: "0px 0px -50px 0px" // Trigger 50px before the section enters the viewport
  };

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('animated');
        // Unobserve after animation to improve performance
        observer.unobserve(entry.target);
      }
    });
  }, observerOptions);

  // Observe all scroll-animate elements
  document.querySelectorAll('.scroll-animate').forEach(el => {
    observer.observe(el);
  });
});







//oigbfduogh
// Mobile sidebar toggle
document.addEventListener("DOMContentLoaded", function () {
  const menuToggle = document.querySelector('.menu-toggle');
  const sidebar = document.querySelector('.sidebar');

  if (menuToggle && sidebar) {
    menuToggle.addEventListener('click', function () {
      sidebar.classList.toggle('active');
    });
  }

  // Close sidebar when clicking a nav link (optional, improves UX)
  const navLinks = document.querySelectorAll('.nav-links li, .sidebar-bottom li');
  navLinks.forEach(link => {
    link.addEventListener('click', function () {
      sidebar.classList.remove('active');
    });
  });

  // Close sidebar when clicking a nav link (optional, improves UX)
  const close = document.querySelectorAll('.close-sidebar');
  close.forEach(link => {
    link.addEventListener('click', function () {
      sidebar.classList.remove('active');
    });
  });
});






// View All/View Less Toggle for Special Offers
document.addEventListener("DOMContentLoaded", function () {
  const viewAllToggle = document.querySelector('#view-all-toggle');
  const offersList = document.querySelector('.offers-list');

  if (viewAllToggle && offersList) {
    viewAllToggle.addEventListener('click', function (e) {
      e.preventDefault(); // Prevent default link behavior
      offersList.classList.toggle('show-all');

      // Toggle text between "View all" and "View Less"
      const isShowingAll = offersList.classList.contains('show-all');
      viewAllToggle.childNodes[0].textContent = isShowingAll ? 'View Less ' : 'View all ';
    });
  }
});





// View All/View Less Toggle for Category Section
document.addEventListener("DOMContentLoaded", function () {
  const viewAllToggle = document.querySelector('.category-header .view-all-games');
  const categoryList = document.querySelector('.category-list');

  if (viewAllToggle && categoryList) {
    viewAllToggle.addEventListener('click', function (e) {
      e.preventDefault(); // Prevent default link behavior
      categoryList.classList.toggle('show-all');

      // Toggle text between "View all" and "View Less"
      const isShowingAll = categoryList.classList.contains('show-all');
      viewAllToggle.childNodes[0].textContent = isShowingAll ? 'View Less ' : 'View all ';
    });
  }
});



// View All/View Less Toggle for Developer Section
document.addEventListener("DOMContentLoaded", function () {
  const devViewAllToggle = document.querySelector('.dev-se .game-header .view-all-games');
  const devList = document.querySelector('.dev-se .dev-list');

  if (devViewAllToggle && devList) {
    devViewAllToggle.addEventListener('click', function (e) {
      e.preventDefault(); // Prevent default link behavior
      devList.classList.toggle('show-all');

      // Toggle text between "View all" and "View Less"
      const isShowingAll = devList.classList.contains('show-all');
      devViewAllToggle.childNodes[0].textContent = isShowingAll ? 'View Less ' : 'View all ';
    });
  }
});

// Horizontal drag scrolling with auto-animate for developer cards
document.addEventListener("DOMContentLoaded", function () {
  const devListContainer = document.querySelector('.dev-se .dev-list-container');
  const devList = document.querySelector('.dev-se .dev-list');

  if (devListContainer && devList) {
    // Drag scroll functionality
    let isDown = false;
    let isDragging = false;
    let startX;
    let scrollLeft;
    let animationId;
    let scrollSpeed = 1; // pixels per frame (adjust for speed)
    let direction = 1; // 1 for right, -1 for left
    let isPaused = false;

    // Clone cards for infinite loop effect
    const cards = devList.querySelectorAll('.dev-card');
    cards.forEach(card => {
      devList.appendChild(card.cloneNode(true));
    });

    // Auto-scroll function
    function autoScroll() {
      if (!isPaused && !isDragging) {
        const maxScroll = devList.scrollWidth - devListContainer.clientWidth;

        if (devListContainer.scrollLeft >= maxScroll - 1) {
          // When reaching end, reset to start for infinite effect
          devListContainer.scrollLeft = 0;
        } else if (devListContainer.scrollLeft <= 0) {
          // When at start, scroll right
          direction = 1;
        }

        devListContainer.scrollLeft += scrollSpeed * direction;
      }
      animationId = requestAnimationFrame(autoScroll);
    }

    // Start auto-scroll
    autoScroll();

    // Pause on hover
    devListContainer.addEventListener('mouseenter', () => {
      isPaused = true;
    });

    devListContainer.addEventListener('mouseleave', () => {
      isPaused = false;
    });

    // Drag events
    devListContainer.addEventListener('mousedown', (e) => {
      isDown = true;
      isDragging = true;
      startX = e.pageX - devListContainer.offsetLeft;
      scrollLeft = devListContainer.scrollLeft;
      devListContainer.style.cursor = 'grabbing';
      devListContainer.style.scrollBehavior = 'auto';
    });

    devListContainer.addEventListener('mouseleave', () => {
      isDown = false;
      isDragging = false;
      devListContainer.style.cursor = 'grab';
      devListContainer.style.scrollBehavior = 'smooth';
    });

    devListContainer.addEventListener('mouseup', () => {
      isDown = false;
      isDragging = false;
      devListContainer.style.cursor = 'grab';
      devListContainer.style.scrollBehavior = 'smooth';
      // Reset auto-scroll direction based on last drag direction
      direction = (devListContainer.scrollLeft > scrollLeft) ? 1 : -1;
    });

    devListContainer.addEventListener('mousemove', (e) => {
      if (!isDown) return;
      e.preventDefault();
      const x = e.pageX - devListContainer.offsetLeft;
      const walk = (x - startX) * 2;
      devListContainer.scrollLeft = scrollLeft - walk;
    });

    // Touch events for mobile
    devListContainer.addEventListener('touchstart', (e) => {
      isDown = true;
      isDragging = true;
      startX = e.touches[0].pageX - devListContainer.offsetLeft;
      scrollLeft = devListContainer.scrollLeft;
      cancelAnimationFrame(animationId);
    });

    devListContainer.addEventListener('touchend', () => {
      isDown = false;
      isDragging = false;
      autoScroll(); // Restart animation
    });

    devListContainer.addEventListener('touchmove', (e) => {
      if (!isDown) return;
      const x = e.touches[0].pageX - devListContainer.offsetLeft;
      const walk = (x - startX) * 2;
      devListContainer.scrollLeft = scrollLeft - walk;
    });

    // Smooth scroll on wheel event
    devListContainer.addEventListener('wheel', (e) => {
      e.preventDefault();
      devListContainer.scrollLeft += e.deltaY * 0.5;
    }, { passive: false });

    // Clean up animation frame when leaving page
    window.addEventListener('beforeunload', () => {
      cancelAnimationFrame(animationId);
    });
  }
});





document.addEventListener('DOMContentLoaded', () => {
  const viewAllToggle = document.getElementById('view-all-games-toggle');
  const gameList = document.querySelector('.game-list-s');

  if (viewAllToggle && gameList) {
    viewAllToggle.addEventListener('click', (e) => {
      e.preventDefault(); // Prevent default link behavior
      gameList.classList.toggle('show-all');
      viewAllToggle.textContent = gameList.classList.contains('show-all')
        ? 'Show less'
        : 'View all';
    });
  }
});


document.addEventListener('DOMContentLoaded', () => {
  const viewAllToggle = document.getElementById('view-all-games-toggle1');
  const gameList = document.querySelector('.game-list-s1');

  if (viewAllToggle && gameList) {
    viewAllToggle.addEventListener('click', (e) => {
      e.preventDefault(); // Prevent default link behavior
      gameList.classList.toggle('show-all');
      viewAllToggle.textContent = gameList.classList.contains('show-all')
        ? 'Show less'
        : 'View all';
    });
  }
});

document.addEventListener('DOMContentLoaded', () => {
  const viewAllToggle = document.getElementById('view-all-games-toggle2');
  const gameList = document.querySelector('.game-list-s2');

  if (viewAllToggle && gameList) {
    viewAllToggle.addEventListener('click', (e) => {
      e.preventDefault(); // Prevent default link behavior
      gameList.classList.toggle('show-all');
      viewAllToggle.textContent = gameList.classList.contains('show-all')
        ? 'Show less'
        : 'View all';
    });
  }
});

document.addEventListener('DOMContentLoaded', () => {
  const viewAllToggle = document.getElementById('view-all-games-toggle3');
  const gameList = document.querySelector('.game-list-s3');

  if (viewAllToggle && gameList) {
    viewAllToggle.addEventListener('click', (e) => {
      e.preventDefault(); // Prevent default link behavior
      gameList.classList.toggle('show-all');
      viewAllToggle.textContent = gameList.classList.contains('show-all')
        ? 'Show less'
        : 'View all';
    });
  }
});

document.addEventListener('DOMContentLoaded', () => {
  const viewAllToggle = document.getElementById('view-all-games-toggle4');
  const gameList = document.querySelector('.game-list-s4');

  if (viewAllToggle && gameList) {
    viewAllToggle.addEventListener('click', (e) => {
      e.preventDefault(); // Prevent default link behavior
      gameList.classList.toggle('show-all');
      viewAllToggle.textContent = gameList.classList.contains('show-all')
        ? 'Show less'
        : 'View all';
    });
  }
});
