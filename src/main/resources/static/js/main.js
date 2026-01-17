/**
 * main.js - 메인 페이지 JavaScript
 */

document.addEventListener('DOMContentLoaded', function() {
    // 메인 페이지 특화 기능 초기화
    initHeroAnimation();
});

/**
 * Hero Section Animation
 */
function initHeroAnimation() {
    const heroSection = document.querySelector('.hero-section');
    
    if (heroSection) {
        // Add fade-in animation on load
        heroSection.style.opacity = '0';
        heroSection.style.transform = 'translateY(20px)';
        heroSection.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        
        setTimeout(function() {
            heroSection.style.opacity = '1';
            heroSection.style.transform = 'translateY(0)';
        }, 100);
    }
}
