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
    const existingAttachmentsList = document.getElementById('existingAttachmentsList');

    if(!uploadArea || !fileInput) return;

    uploadArea.addEventListener('click', () => fileInput.click());

    fileInput.addEventListener('change', handleFiles);

    if (existingAttachmentsList) {
        existingAttachmentsList.addEventListener('click', function (e) {
            const removeButton = e.target.closest('.existing-file-remove');
            if (!removeButton) {
                return;
            }

            const fileItem = removeButton.closest('.existing-file');
            if (fileItem) {
                fileItem.remove();
            }
        });
    }

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

    if (Array.isArray(initialParticipantIds) && initialParticipantIds.length > 0) {
        initialParticipantIds.forEach(participantId => addParticipant(String(participantId)));
    }
    
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

    const ownerSelect = document.querySelector('select[name="owner"]');
    if (ownerSelect && !ownerSelect.value) {
        if (activity.ownerId) {
            ownerSelect.value = String(activity.ownerId);
        } else if (activity.createdBy) {
            ownerSelect.value = String(activity.createdBy);
        }
    }

    if ((!hiddenInput || !hiddenInput.value) && activity.participantIds && activity.participantIds.length > 0 && typeof addParticipant === 'function') {
        activity.participantIds.forEach(participantId => {
            addParticipant(String(participantId));
        });
    }
}

// --- 4. OVERRIDE FORM SUBMIT (Khu vực mới thêm) ---
document.getElementById('activityForm').addEventListener('submit', function(e) {
    e.preventDefault(); // Chặn hành vi submit mặc định
    
    // Đổi chữ nút submit để báo hiệu
    const submitBtn = this.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang lưu...';
    submitBtn.disabled = true;

    // Lấy toàn bộ dữ liệu từ form (bao gồm cả input hidden và fileInput hiện tại)
    const formData = new FormData(this);

    // Gửi bằng AJAX
    fetch(this.action, {
        method: 'POST',
        body: formData
    })
    .then(response => {
        // Trình duyệt tự động follow redirect của response
        if (response.redirected) {
            window.location.href = response.url; // Điều hướng trang nếu server redirect
        } else {
            // Nếu không có redirect (ví dụ có lỗi trên server, server rớt lại form)
            return response.text().then(html => {
                document.open();
                document.write(html);
                document.close();
            });
        }
    })
    .catch(error => {
        console.error('Lỗi khi submit form:', error);
        alert('Có lỗi xảy ra khi lưu nội dung. Vui lòng kiểm tra console.');
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    });
});
