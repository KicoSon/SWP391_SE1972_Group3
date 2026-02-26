/**
 * Sidebar Component JavaScript
 * Handles sidebar toggle, mobile responsive behavior, and navigation
 */

class SidebarManager {
    constructor() {
        this.sidebar = document.getElementById('sidebar');
        this.sidebarToggle = document.getElementById('sidebarToggle');
        this.sidebarOverlay = document.getElementById('sidebarOverlay');
        this.isCollapsed = false;
        this.isMobile = window.innerWidth <= 768;
        
        this.init();
    }
    
    init() {
        // Check for saved sidebar state
        const savedState = localStorage.getItem('sidebarCollapsed');
        if (savedState === 'true' && !this.isMobile) {
            this.collapse();
        }
        
        // Bind events
        this.bindEvents();
        
        // Handle window resize
        this.handleResize();
        
        // Set active navigation
        this.setActiveNavigation();
        
        // Initialize submenu toggles
        this.initializeSubmenus();
    }
    
    bindEvents() {
        // Toggle button click
        if (this.sidebarToggle) {
            this.sidebarToggle.addEventListener('click', (e) => {
                e.preventDefault();
                this.toggle();
            });
        }
        
        // Overlay click (mobile)
        if (this.sidebarOverlay) {
            this.sidebarOverlay.addEventListener('click', () => {
                this.close();
            });
        }
        
        // Window resize
        window.addEventListener('resize', () => {
            this.handleResize();
        });
        
        // Navigation link clicks
        this.bindNavigationEvents();
        
        // Escape key to close sidebar on mobile
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.isMobile && this.isOpen()) {
                this.close();
            }
        });
    }
    
    bindNavigationEvents() {
        const navLinks = document.querySelectorAll('.nav-link');
        navLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                // Don't prevent default for actual navigation
                // Just handle mobile sidebar closing
                if (this.isMobile) {
                    setTimeout(() => {
                        this.close();
                    }, 150);
                }
            });
        });
    }
    
    toggle() {
        if (this.isMobile) {
            this.isOpen() ? this.close() : this.open();
        } else {
            this.isCollapsed ? this.expand() : this.collapse();
        }
    }
    
    collapse() {
        if (this.isMobile) return;
        
        this.sidebar.classList.add('collapsed');
        this.isCollapsed = true;
        localStorage.setItem('sidebarCollapsed', 'true');
        
        // Update main content margin
        this.updateMainContentMargin(80);
        
        // Trigger custom event
        this.dispatchEvent('sidebar:collapsed');
    }
    
    expand() {
        if (this.isMobile) return;
        
        this.sidebar.classList.remove('collapsed');
        this.isCollapsed = false;
        localStorage.setItem('sidebarCollapsed', 'false');
        
        // Update main content margin
        this.updateMainContentMargin(280);
        
        // Trigger custom event
        this.dispatchEvent('sidebar:expanded');
    }
    
    open() {
        if (!this.isMobile) return;
        
        this.sidebar.classList.add('active');
        this.sidebarOverlay.classList.add('active');
        document.body.style.overflow = 'hidden';
        
        // Trigger custom event
        this.dispatchEvent('sidebar:opened');
    }
    
    close() {
        if (!this.isMobile) return;
        
        this.sidebar.classList.remove('active');
        this.sidebarOverlay.classList.remove('active');
        document.body.style.overflow = '';
        
        // Trigger custom event
        this.dispatchEvent('sidebar:closed');
    }
    
    isOpen() {
        return this.sidebar.classList.contains('active');
    }
    
    handleResize() {
        const wasMobile = this.isMobile;
        this.isMobile = window.innerWidth <= 768;
        
        if (wasMobile !== this.isMobile) {
            if (this.isMobile) {
                // Switching to mobile
                this.sidebar.classList.remove('collapsed');
                this.close();
                this.updateMainContentMargin(0);
            } else {
                // Switching to desktop
                this.sidebar.classList.remove('active');
                this.sidebarOverlay.classList.remove('active');
                document.body.style.overflow = '';
                
                // Restore saved collapse state
                const savedState = localStorage.getItem('sidebarCollapsed');
                if (savedState === 'true') {
                    this.collapse();
                } else {
                    this.updateMainContentMargin(280);
                }
            }
        }
    }
    
    updateMainContentMargin(marginLeft) {
        const mainContent = document.querySelector('.main-content');
        if (mainContent) {
            mainContent.style.marginLeft = `${marginLeft}px`;
            mainContent.style.transition = 'margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1)';
        }
    }
    
    setActiveNavigation() {
        const currentPath = window.location.pathname;
        const navItems = document.querySelectorAll('.nav-item');
        
        navItems.forEach(item => {
            const link = item.querySelector('.nav-link');
            if (link) {
                const href = link.getAttribute('href');
                if (href && currentPath.includes(href.split('/').pop())) {
                    item.classList.add('active');
                } else {
                    item.classList.remove('active');
                }
            }
        });
    }
    
    dispatchEvent(eventName, data = {}) {
        const event = new CustomEvent(eventName, {
            detail: data,
            bubbles: true
        });
        document.dispatchEvent(event);
    }
    
    // Public API methods
    getState() {
        return {
            isCollapsed: this.isCollapsed,
            isMobile: this.isMobile,
            isOpen: this.isOpen()
        };
    }
    
    // Method to programmatically set navigation active state
    setActiveNav(href) {
        const navItems = document.querySelectorAll('.nav-item');
        navItems.forEach(item => {
            const link = item.querySelector('.nav-link');
            if (link && link.getAttribute('href') === href) {
                item.classList.add('active');
            } else {
                item.classList.remove('active');
            }
        });
    }
    
    // Initialize submenu functionality
    initializeSubmenus() {
        const submenuToggles = document.querySelectorAll('.has-submenu > .nav-toggle');
        
        submenuToggles.forEach(toggle => {
            toggle.addEventListener('click', (e) => {
                e.preventDefault();
                const parentItem = toggle.closest('.has-submenu');
                const isOpen = parentItem.classList.contains('open');
                
                // Close all other submenus
                document.querySelectorAll('.has-submenu.open').forEach(item => {
                    if (item !== parentItem) {
                        item.classList.remove('open');
                    }
                });
                
                // Toggle current submenu
                if (isOpen) {
                    parentItem.classList.remove('open');
                } else {
                    parentItem.classList.add('open');
                }
            });
        });
        
        // Handle submenu item clicks
        const submenuItems = document.querySelectorAll('.nav-subitem .nav-link');
        submenuItems.forEach(item => {
            item.addEventListener('click', () => {
                // Remove active class from all submenu items
                document.querySelectorAll('.nav-subitem').forEach(subitem => {
                    subitem.classList.remove('active');
                });
                
                // Add active class to clicked item
                item.closest('.nav-subitem').classList.add('active');
                
                // Keep parent submenu open
                const parentSubmenu = item.closest('.has-submenu');
                if (parentSubmenu) {
                    parentSubmenu.classList.add('open');
                }
            });
        });
        
        // Auto-open submenu if current page is a submenu item
        const currentPath = window.location.pathname + window.location.search;
        submenuItems.forEach(item => {
            const href = item.getAttribute('href');
            if (href && currentPath.includes(href.replace(/^.*\//, ''))) {
                const parentSubmenu = item.closest('.has-submenu');
                const subItem = item.closest('.nav-subitem');
                
                if (parentSubmenu) {
                    parentSubmenu.classList.add('open');
                }
                if (subItem) {
                    subItem.classList.add('active');
                }
            }
        });
    }
}

// Tooltip functionality for collapsed sidebar
class SidebarTooltip {
    constructor() {
        this.init();
    }
    
    init() {
        document.addEventListener('sidebar:collapsed', () => {
            this.enableTooltips();
        });
        
        document.addEventListener('sidebar:expanded', () => {
            this.disableTooltips();
        });
    }
    
    enableTooltips() {
        const navLinks = document.querySelectorAll('.nav-link');
        navLinks.forEach(link => {
            const text = link.querySelector('.nav-text');
            if (text) {
                link.setAttribute('title', text.textContent.trim());
                link.setAttribute('data-tooltip', text.textContent.trim());
            }
        });
    }
    
    disableTooltips() {
        const navLinks = document.querySelectorAll('.nav-link');
        navLinks.forEach(link => {
            link.removeAttribute('title');
            link.removeAttribute('data-tooltip');
        });
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    // Initialize sidebar manager
    window.sidebarManager = new SidebarManager();
    
    // Initialize tooltips
    new SidebarTooltip();
    
    // Add smooth scrolling for navigation
    document.querySelectorAll('.nav-link[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
});

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { SidebarManager, SidebarTooltip };
}