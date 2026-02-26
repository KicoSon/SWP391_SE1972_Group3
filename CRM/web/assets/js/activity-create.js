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
        