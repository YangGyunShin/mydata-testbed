/**
 * common.js - 공통 JavaScript
 */

document.addEventListener('DOMContentLoaded', function() {
    // Mobile Menu Toggle
    initMobileMenu();
    
    // GNB Submenu (Mobile)
    initMobileSubmenu();
    
    // FAQ Accordion
    initFaqAccordion();
    
    // Terms Toggle
    initTermsToggle();
    
    // All Agree Checkbox
    initAllAgreeCheckbox();
    
    // File Upload
    initFileUpload();
});

/**
 * Mobile Menu Toggle
 */
function initMobileMenu() {
    const menuBtn = document.querySelector('.mobile-menu-btn');
    const gnb = document.querySelector('.gnb');
    
    if (menuBtn && gnb) {
        menuBtn.addEventListener('click', function() {
            this.classList.toggle('active');
            gnb.classList.toggle('active');
            document.body.classList.toggle('menu-open');
        });
    }
}

/**
 * Mobile Submenu Toggle
 */
function initMobileSubmenu() {
    const gnbItems = document.querySelectorAll('.gnb-item');
    
    gnbItems.forEach(function(item) {
        const link = item.querySelector('.gnb-link');
        
        if (link && window.innerWidth <= 768) {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                item.classList.toggle('active');
            });
        }
    });
    
    // Handle resize
    window.addEventListener('resize', function() {
        if (window.innerWidth > 768) {
            gnbItems.forEach(function(item) {
                item.classList.remove('active');
            });
        }
    });
}

/**
 * FAQ Accordion
 */
function initFaqAccordion() {
    const faqItems = document.querySelectorAll('.faq-item');
    
    faqItems.forEach(function(item) {
        const question = item.querySelector('.faq-question');
        
        if (question) {
            question.addEventListener('click', function() {
                // Close other items
                faqItems.forEach(function(otherItem) {
                    if (otherItem !== item) {
                        otherItem.classList.remove('active');
                    }
                });
                
                // Toggle current item
                item.classList.toggle('active');
            });
        }
    });
}

/**
 * Terms Toggle
 */
function initTermsToggle() {
    const termsToggles = document.querySelectorAll('.terms-toggle');
    
    termsToggles.forEach(function(toggle) {
        toggle.addEventListener('click', function(e) {
            e.stopPropagation();
            const termsBox = this.closest('.terms-box');
            const content = termsBox.querySelector('.terms-content');
            
            if (content) {
                content.classList.toggle('show');
                this.textContent = content.classList.contains('show') ? '닫기' : '보기';
            }
        });
    });
}

/**
 * All Agree Checkbox
 */
function initAllAgreeCheckbox() {
    const allAgreeCheckbox = document.querySelector('#allAgree');
    const agreeCheckboxes = document.querySelectorAll('.terms-box input[type="checkbox"]');
    
    if (allAgreeCheckbox && agreeCheckboxes.length > 0) {
        // All agree checkbox click
        allAgreeCheckbox.addEventListener('change', function() {
            agreeCheckboxes.forEach(function(checkbox) {
                checkbox.checked = allAgreeCheckbox.checked;
            });
        });
        
        // Individual checkbox click
        agreeCheckboxes.forEach(function(checkbox) {
            checkbox.addEventListener('change', function() {
                const allChecked = Array.from(agreeCheckboxes).every(function(cb) {
                    return cb.checked;
                });
                allAgreeCheckbox.checked = allChecked;
            });
        });
    }
}

/**
 * File Upload
 */
function initFileUpload() {
    const fileInputs = document.querySelectorAll('.file-upload input[type="file"]');
    
    fileInputs.forEach(function(input) {
        input.addEventListener('change', function() {
            const fileName = this.files[0] ? this.files[0].name : '파일을 선택하세요';
            const fileNameEl = this.closest('.file-upload').querySelector('.file-name');
            
            if (fileNameEl) {
                fileNameEl.textContent = fileName;
            }
        });
    });
}

/**
 * Form Validation
 */
function validateForm(formElement) {
    const requiredFields = formElement.querySelectorAll('[required]');
    let isValid = true;
    
    requiredFields.forEach(function(field) {
        clearFieldError(field);
        
        if (!field.value.trim()) {
            showFieldError(field, '필수 입력 항목입니다.');
            isValid = false;
        }
    });
    
    // Email validation
    const emailFields = formElement.querySelectorAll('input[type="email"]');
    emailFields.forEach(function(field) {
        if (field.value && !isValidEmail(field.value)) {
            showFieldError(field, '올바른 이메일 형식이 아닙니다.');
            isValid = false;
        }
    });
    
    return isValid;
}

function showFieldError(field, message) {
    field.classList.add('is-invalid');
    
    let feedback = field.parentNode.querySelector('.invalid-feedback');
    if (!feedback) {
        feedback = document.createElement('div');
        feedback.className = 'invalid-feedback';
        field.parentNode.appendChild(feedback);
    }
    feedback.textContent = message;
}

function clearFieldError(field) {
    field.classList.remove('is-invalid');
    const feedback = field.parentNode.querySelector('.invalid-feedback');
    if (feedback) {
        feedback.remove();
    }
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

/**
 * Confirm Dialog
 */
function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

/**
 * Toast Notification
 */
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    
    document.body.appendChild(toast);
    
    // Show animation
    setTimeout(function() {
        toast.classList.add('show');
    }, 10);
    
    // Hide after 3 seconds
    setTimeout(function() {
        toast.classList.remove('show');
        setTimeout(function() {
            toast.remove();
        }, 300);
    }, 3000);
}

/**
 * Loading Spinner
 */
function showLoading() {
    const loading = document.createElement('div');
    loading.className = 'loading-overlay';
    loading.innerHTML = '<div class="loading-spinner"></div>';
    document.body.appendChild(loading);
}

function hideLoading() {
    const loading = document.querySelector('.loading-overlay');
    if (loading) {
        loading.remove();
    }
}

/**
 * Scroll to Top
 */
function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

/**
 * Debounce Function
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = function() {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}
