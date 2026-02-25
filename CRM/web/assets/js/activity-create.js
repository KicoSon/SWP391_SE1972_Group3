<<<<<<< HEAD
/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


// Sample participants data
const allParticipants = [
    {id: 'viehai', name: 'Viehai', role: 'Manager'},
    {id: 'nam', name: 'Nam', role: 'Senior Sales'},
    {id: 'minh', name: 'Minh', role: 'Sales'},
    {id: 'lan', name: 'Lan', role: 'Sales'},
    {id: 'hung', name: 'Hùng', role: 'Technical Support'},
    {id: 'linh', name: 'Linh', role: 'Customer Service'}
];

let selectedParticipants = [];

// Reminder Toggle
const enableReminder = document.getElementById('enableReminder');
const reminderOptions = document.getElementById('reminderOptions');
const reminderType = document.querySelector('select[name="reminder_type"]');
const customReminderTime = document.getElementById('customReminderTime');

enableReminder.addEventListener('change', function () {
    if (this.checked) {
        reminderOptions.classList.add('active');
    } else {
        reminderOptions.classList.remove('active');
    }
});

reminderType.addEventListener('change', function () {
    if (this.value === 'custom') {
        customReminderTime.style.display = 'block';
    } else {
        customReminderTime.style.display = 'none';
    }
});

// Participants Tags Input
const tagsContainer = document.getElementById('tagsContainer');
const participantInput = document.getElementById('participantInput');
const participantSuggestions = document.getElementById('participantSuggestions');

participantInput.addEventListener('input', function () {
    const searchTerm = this.value.toLowerCase().trim();

    if (searchTerm.length > 0) {
        const filtered = allParticipants.filter(p =>
            !selectedParticipants.includes(p.id) &&
                    (p.name.toLowerCase().includes(searchTerm) || p.role.toLowerCase().includes(searchTerm))
        );

        if (filtered.length > 0) {
            participantSuggestions.innerHTML = filtered.map(p => `
                            <div class="suggestion-item" data-id="${p.id}">
                                <div class="suggestion-avatar">${p.name.charAt(0).toUpperCase()}</div>
                                <div>
                                    <div style="font-weight: 600;">${p.name}</div>
                                    <div style="font-size: 12px; color: var(--gray-500);">${p.role}</div>
                                </div>
                            </div>
                        `).join('');
            participantSuggestions.classList.add('active');
        } else {
            participantSuggestions.classList.remove('active');
        }
    } else {
        participantSuggestions.classList.remove('active');
    }
});

participantSuggestions.addEventListener('click', function (e) {
    const suggestionItem = e.target.closest('.suggestion-item');
    if (suggestionItem) {
        const participantId = suggestionItem.dataset.id;
        addParticipant(participantId);
        participantInput.value = '';
        participantSuggestions.classList.remove('active');
    }
});

function addParticipant(id) {
    if (selectedParticipants.includes(id))
        return;

    const participant = allParticipants.find(p => p.id === id);
    if (!participant)
        return;

    selectedParticipants.push(id);

    const tag = document.createElement('div');
    tag.className = 'tag';
    tag.innerHTML = `
                    <span>${participant.name} (${participant.role})</span>
                    <span class="tag-remove" data-id="${id}">×</span>
                `;

    tagsContainer.insertBefore(tag, participantInput);
}

tagsContainer.addEventListener('click', function (e) {
    if (e.target.classList.contains('tag-remove')) {
        const id = e.target.dataset.id;
        selectedParticipants = selectedParticipants.filter(p => p !== id);
        e.target.parentElement.remove();
    } else if (e.target === tagsContainer) {
        participantInput.focus();
    }
});

// Click outside to close suggestions
document.addEventListener('click', function (e) {
    if (!participantSuggestions.contains(e.target) && e.target !== participantInput) {
        participantSuggestions.classList.remove('active');
    }
});

// File Upload
const fileUploadArea = document.getElementById('fileUploadArea');
const fileInput = document.getElementById('fileInput');
const uploadedFiles = document.getElementById('uploadedFiles');
let files = [];

fileUploadArea.addEventListener('click', () => fileInput.click());

fileUploadArea.addEventListener('dragover', (e) => {
    e.preventDefault();
    fileUploadArea.classList.add('drag-over');
});

fileUploadArea.addEventListener('dragleave', () => {
    fileUploadArea.classList.remove('drag-over');
});

fileUploadArea.addEventListener('drop', (e) => {
    e.preventDefault();
    fileUploadArea.classList.remove('drag-over');
    handleFiles(e.dataTransfer.files);
});

fileInput.addEventListener('change', (e) => {
    handleFiles(e.target.files);
});

function handleFiles(fileList) {
    Array.from(fileList).forEach(file => {
        if (file.size > 10 * 1024 * 1024) {
            alert(`File ${file.name} quá lớn (max 10MB)`);
            return;
        }

        files.push(file);
        displayFile(file);
    });
}

function displayFile(file) {
    const fileDiv = document.createElement('div');
    fileDiv.className = 'uploaded-file';

    const fileIcon = getFileIcon(file.name);
    const fileSize = formatFileSize(file.size);

    fileDiv.innerHTML = `
                    <div class="file-info">
                        <span class="file-icon">${fileIcon}</span>
                        <div class="file-details">
                            <div class="file-name">${file.name}</div>
                            <div class="file-size">${fileSize}</div>
                        </div>
                    </div>
                    <button type="button" class="file-remove" data-file="${file.name}">Xóa</button>
                `;

    uploadedFiles.appendChild(fileDiv);
}

uploadedFiles.addEventListener('click', (e) => {
    if (e.target.classList.contains('file-remove')) {
        const fileName = e.target.dataset.file;
        files = files.filter(f => f.name !== fileName);
        e.target.closest('.uploaded-file').remove();
    }
});

function getFileIcon(filename) {
    const ext = filename.split('.').pop().toLowerCase();
    const icons = {
        'pdf': '📄',
        'doc': '📝', 'docx': '📝',
        'xls': '📊', 'xlsx': '📊',
        'ppt': '📊', 'pptx': '📊',
        'jpg': '🖼️', 'jpeg': '🖼️', 'png': '🖼️',
        'mp3': '🎵', 'wav': '🎵'
    };
    return icons[ext] || '📎';
}

function formatFileSize(bytes) {
    if (bytes === 0)
        return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

// Form Submission
document.getElementById('activityForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const formData = new FormData(this);

    // Add participants
    formData.append('participants', JSON.stringify(selectedParticipants));

    // Add files
    files.forEach((file, index) => {
        formData.append(`file_${index}`, file);
    });

    // Here you would send formData to your server
    console.log('Form Data:', Object.fromEntries(formData));
    console.log('Participants:', selectedParticipants);
    console.log('Files:', files);

    alert('Form submitted successfully! Check console for data.');
});

// Auto-fill date/time with current values
const now = new Date();
const dateInput = document.querySelector('input[name="date"]');
const timeInput = document.querySelector('input[name="time"]');

dateInput.value = now.toISOString().split('T')[0];
timeInput.value = now.toTimeString().slice(0, 5);
        
=======
/* =========================================
 * XỬ LÝ LOGIC CHO TRANG TẠO ACTIVITY
 * ========================================= */

// --- 1. LOGIC PARTICIPANTS (NGƯỜI PHỐI HỢP) ---
const participantsSource = (typeof allParticipantsData !== 'undefined') ? allParticipantsData : [];
let selectedParticipants = []; 

const tagsContainer = document.getElementById('tagsContainer');
const participantInput = document.getElementById('participantInput');
const participantSuggestions = document.getElementById('participantSuggestions');
const hiddenInput = document.getElementById('participantIdsHidden');
const form = document.getElementById('activityForm');

if (participantInput) {
    participantInput.addEventListener('input', function () {
        const searchTerm = this.value.toLowerCase().trim();

        if (searchTerm.length > 0) {
            const filtered = participantsSource.filter(p =>
                !selectedParticipants.includes(p.id) &&
                        (p.name.toLowerCase().includes(searchTerm) || p.role.toLowerCase().includes(searchTerm))
            );

            if (filtered.length > 0) {
                participantSuggestions.innerHTML = filtered.map(p => `
                    <div class="suggestion-item" data-id="${p.id}">
                        <div class="suggestion-avatar">${p.name.charAt(0).toUpperCase()}</div>
                        <div>
                            <div style="font-weight: 600;">${p.name}</div>
                            <div style="font-size: 12px; color: #666;">${p.role}</div>
                        </div>
                    </div>
                `).join('');
                participantSuggestions.classList.add('active');
            } else {
                participantSuggestions.classList.remove('active');
            }
        } else {
            participantSuggestions.classList.remove('active');
        }
    });

    participantSuggestions.addEventListener('click', function (e) {
        const suggestionItem = e.target.closest('.suggestion-item');
        if (suggestionItem) {
            const participantId = suggestionItem.dataset.id;
            addParticipant(participantId);

            participantInput.value = '';
            participantSuggestions.classList.remove('active');
            participantInput.focus();
        }
    });

    tagsContainer.addEventListener('click', function (e) {
        if (e.target.classList.contains('tag-remove')) {
            const id = e.target.dataset.id;
            selectedParticipants = selectedParticipants.filter(p => p !== id);
            updateHiddenInput(); 
            e.target.parentElement.remove();
        } else if (e.target === tagsContainer) {
            participantInput.focus();
        }
    });
}

function addParticipant(id) {
    if (selectedParticipants.includes(id)) return;

    const participant = participantsSource.find(p => p.id === id);
    if (!participant) return;

    selectedParticipants.push(id);
    updateHiddenInput(); 

    const tag = document.createElement('div');
    tag.className = 'tag';
    tag.innerHTML = `
        <span>${participant.name} (${participant.role})</span>
        <span class="tag-remove" data-id="${id}">×</span>
    `;
    tagsContainer.insertBefore(tag, participantInput);
}

document.addEventListener('click', function (e) {
    if (participantSuggestions && !participantSuggestions.contains(e.target) && e.target !== participantInput) {
        participantSuggestions.classList.remove('active');
    }
});

function updateHiddenInput() {
    if(hiddenInput) {
        hiddenInput.value = selectedParticipants.join(',');
        console.log("Current Participants IDs:", hiddenInput.value); 
    }
}

// --- 2. LOGIC LỌC RELATED TO (LEAD & OPPORTUNITY) ---
function filterRelatedTo() {
    var customerSelect = document.getElementById("customerSelect");
    var relatedSelect = document.getElementById("relatedSelect");
    
    if(!customerSelect || !relatedSelect) return;

    var customerId = customerSelect.value;
    var options = relatedSelect.querySelectorAll("option");

    relatedSelect.value = "";

    options.forEach(function (opt) {
        if (opt.value === "") {
            opt.style.display = ""; 
            return;
        }

        var val = opt.value; 
        var dataCust = opt.getAttribute("data-customer");

        if (!customerId || customerId === "") {
            if (val.startsWith("lead-")) {
                opt.style.display = ""; 
            } else {
                opt.style.display = "none"; 
            }
        } 
        else {
            if (val.startsWith("opp-") && dataCust === customerId) {
                opt.style.display = ""; 
            } else {
                opt.style.display = "none"; 
            }
        }
    });
}

// --- 3. LOGIC KÉO THẢ FILE ĐÍNH KÈM (MỚI THÊM) ---
function initFileUpload() {
    const uploadArea = document.getElementById('fileUploadArea');
    const fileInput = document.getElementById('fileInput');
    const uploadedFilesContainer = document.getElementById('uploadedFiles');

    if(!uploadArea || !fileInput) return;

    uploadArea.addEventListener('click', () => fileInput.click());

    fileInput.addEventListener('change', handleFiles);

    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.style.borderColor = '#007bff'; 
        uploadArea.style.backgroundColor = '#f0f8ff';
    });

    uploadArea.addEventListener('dragleave', () => {
        uploadArea.style.borderColor = '#ccc'; 
        uploadArea.style.backgroundColor = 'transparent';
    });

    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        uploadArea.style.borderColor = '#ccc';
        uploadArea.style.backgroundColor = 'transparent';
        
        if (e.dataTransfer.files.length > 0) {
            fileInput.files = e.dataTransfer.files;
            handleFiles(); 
        }
    });

    function handleFiles() {
        uploadedFilesContainer.innerHTML = ''; 
        const files = fileInput.files;
        if (files.length === 0) return;

        let html = '<ul style="list-style: none; padding: 0; margin-top: 10px;">';
        for (let i = 0; i < files.length; i++) {
            html += `<li style="padding: 5px 0; color: #28a745;">✅ ${files[i].name} (${(files[i].size / 1024 / 1024).toFixed(2)} MB)</li>`;
        }
        html += '</ul>';
        uploadedFilesContainer.innerHTML = html;
    }
}

// --- INITIALIZE (CHẠY KHI LOAD TRANG) ---
document.addEventListener("DOMContentLoaded", function () {
    // Gọi hàm lọc Khách hàng
    filterRelatedTo();
    
    // Khởi tạo File Upload
    initFileUpload();

    // Auto fill date time (Fix lỗi lệch múi giờ)
    const dateInput = document.querySelector('input[name="date"]');
    const timeInput = document.querySelector('input[name="time"]');
    if (dateInput && timeInput && !dateInput.value) {
        // Lấy giờ địa phương Việt Nam thay vì UTC
        const now = new Date();
        now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
        
        dateInput.value = now.toISOString().split('T')[0];
        timeInput.value = now.toISOString().split('T')[1].slice(0, 5);
    }

    // Load activity data for edit mode (if ID parameter exists)
    const urlParams = new URLSearchParams(window.location.search);
    const activityId = urlParams.get('id');

    if (activityId) {
        fetch(window.location.origin + '/CRM/activities/api/detail?id=' + activityId)
            .then(response => response.json())
            .then(data => populateForm(data))
            .catch(err => console.error('Lỗi tải dữ liệu:', err));
    }
});

// Populate form with activity data (Edit mode)
function populateForm(activity) {
    document.querySelector('input[name="title"]').value = activity.title || '';
    document.querySelector('textarea[name="description"]').value = activity.description || '';
    document.querySelector('select[name="type"]').value = activity.type || 'Call';
    document.querySelector('select[name="status"]').value = activity.status || 'Planned';

    const priorityValue = activity.priority || 'Medium';
    const priorityRadio = document.querySelector(`input[name="priority"][value="${priorityValue}"]`);
    if (priorityRadio)
        priorityRadio.checked = true;

    if (activity.dueDate) {
        const date = new Date(activity.dueDate);
        const dateStr = date.toISOString().split('T')[0];
        const timeStr = String(date.getHours()).padStart(2, '0') + ':' + String(date.getMinutes()).padStart(2, '0');
        document.querySelector('input[name="date"]').value = dateStr;
        document.querySelector('input[name="time"]').value = timeStr;
    }

    if (activity.customerId)
        document.querySelector('select[name="customer"]').value = activity.customerId;

    if (activity.opportunityId) {
        document.querySelector('select[name="related_to"]').value = 'opp-' + activity.opportunityId;
    } else if (activity.leadId) {
        document.querySelector('select[name="related_to"]').value = 'lead-' + activity.leadId;
    }

    if (activity.createdBy)
        document.querySelector('select[name="owner"]').value = activity.createdBy;

    if (activity.participants && activity.participants.length > 0 && typeof allParticipantsData !== 'undefined') {
        activity.participants.forEach(participantName => {
            const participant = allParticipantsData.find(p => p.name === participantName);
            if (participant && typeof addParticipant === 'function')
                addParticipant(participant.id);
        });
    }
}
>>>>>>> develop
