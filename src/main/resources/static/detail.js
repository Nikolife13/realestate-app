const API = 'http://localhost:8080/api';
let currentImages = [];
let currentIndex = 0;
let carouselContainer = null;
let carouselImage = null;
let prevBtn = null;
let nextBtn = null;

async function loadDetail() {
    const id = new URLSearchParams(window.location.search).get('id');
    if (!id) {
        document.getElementById('propertyDetail').innerHTML = '<p>Invalid property ID.</p>';
        return;
    }
    try {
        const res = await fetch(`${API}/properties/${id}`);
        const prop = await res.json();
        renderDetail(prop);
        setupCarousel(prop.imageUrls || []);
    } catch(e) {
        document.getElementById('propertyDetail').innerHTML = '<p>Error loading property.</p>';
        console.error(e);
    }
}

function renderDetail(prop) {
    const container = document.getElementById('propertyDetail');
    container.innerHTML = `
        <h1 class="property-title">${prop.title}</h1>
        <div class="property-address">📍 ${prop.address}, ${prop.city}, ${prop.county}</div>
        <div class="property-price">$${prop.price.toLocaleString()}</div>
        <div>
            <span class="property-rooms">🛏️ ${prop.rooms} rooms</span>
            <span class="property-bathrooms">🚽 ${prop.bathrooms} bathrooms</span>
        </div>
        <p><strong>Description:</strong> ${prop.description || 'No description provided.'}</p>
    `;
}

function setupCarousel(imageUrls) {
    carouselContainer = document.querySelector('.carousel-container');
    carouselImage = document.getElementById('carousel-image');
    prevBtn = document.querySelector('.prev-btn');
    nextBtn = document.querySelector('.next-btn');
    const dotsContainer = document.querySelector('.carousel-dots');

    if (!imageUrls || imageUrls.length === 0) {
        if (carouselContainer) carouselContainer.style.display = 'none';
        return;
    }

    currentImages = imageUrls;
    currentIndex = 0;
    if (carouselContainer) carouselContainer.style.display = 'block';
    updateCarousel();

    if (prevBtn) prevBtn.onclick = () => {
        currentIndex = (currentIndex - 1 + currentImages.length) % currentImages.length;
        updateCarousel();
    };
    if (nextBtn) nextBtn.onclick = () => {
        currentIndex = (currentIndex + 1) % currentImages.length;
        updateCarousel();
    };

    window.addEventListener('keydown', (e) => {
        if (e.key === 'ArrowLeft') {
            currentIndex = (currentIndex - 1 + currentImages.length) % currentImages.length;
            updateCarousel();
        } else if (e.key === 'ArrowRight') {
            currentIndex = (currentIndex + 1) % currentImages.length;
            updateCarousel();
        }
    });

    if (dotsContainer) {
        dotsContainer.innerHTML = '';
        currentImages.forEach((_, idx) => {
            const dot = document.createElement('span');
            dot.className = 'dot' + (idx === currentIndex ? ' active' : '');
            dot.addEventListener('click', () => {
                currentIndex = idx;
                updateCarousel();
            });
            dotsContainer.appendChild(dot);
        });
    }
}

function updateCarousel() {
    if (currentImages.length && carouselImage) {
        carouselImage.src = currentImages[currentIndex];
        const dots = document.querySelectorAll('.dot');
        dots.forEach((dot, idx) => {
            if (idx === currentIndex) dot.classList.add('active');
            else dot.classList.remove('active');
        });
    }
}

document.getElementById('backBtn')?.addEventListener('click', () => {
    window.location.href = 'dashboard.html';
});

loadDetail();