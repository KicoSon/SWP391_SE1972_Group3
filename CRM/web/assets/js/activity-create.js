/* * Xử lý Logic cho trang Tạo Activity
 * Đảm bảo dữ liệu được đồng bộ vào Input Hidden trước khi Submit
 */

// 1. Lấy dữ liệu từ JSP (Biến toàn cục đã khai báo ở file .jsp)
// Nếu không tìm thấy (do lỗi load), gán mảng rỗng để tránh crash
const participantsSource = (typeof allParticipantsData !== 'undefined') ? allParticipantsData : [];

let selectedParticipants = []; // Mảng chứa ID các user đã chọn

// DOM Elements
const tagsContainer = document.getElementById('tagsContainer');
const participantInput = document.getElementById('participantInput');
const participantSuggestions = document.getElementById('participantSuggestions');
const hiddenInput = document.getElementById('participantIdsHidden');
const form = document.getElementById('activityForm');

// --- LOGIC TÌM KIẾM & GỢI Ý (Auto-complete) ---

participantInput.addEventListener('input', function () {
    const searchTerm = this.value.toLowerCase().trim();

    if (searchTerm.length > 0) {
        // Lọc danh sách: Chưa được chọn AND (Trùng tên OR Trùng role)
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

// Sự kiện chọn từ danh sách gợi ý
participantSuggestions.addEventListener('click', function (e) {
    const suggestionItem = e.target.closest('.suggestion-item');
    if (suggestionItem) {
        const participantId = suggestionItem.dataset.id;
        addParticipant(participantId);

        // Reset input
        participantInput.value = '';
        participantSuggestions.classList.remove('active');
        participantInput.focus();
    }
});

// --- LOGIC THÊM / XÓA PARTICIPANT ---

function addParticipant(id) {
    // Kiểm tra trùng
    if (selectedParticipants.includes(id))
        return;

    // Tìm thông tin user
    const participant = participantsSource.find(p => p.id === id);
    if (!participant)
        return;

    // Thêm vào mảng quản lý
    selectedParticipants.push(id);
    updateHiddenInput(); // Cập nhật input hidden ngay

    // Tạo thẻ Tag giao diện
    const tag = document.createElement('div');
    tag.className = 'tag';
    tag.innerHTML = `
        <span>${participant.name} (${participant.role})</span>
        <span class="tag-remove" data-id="${id}">×</span>
    `;

    // Chèn tag vào trước ô input
    tagsContainer.insertBefore(tag, participantInput);
}

// Sự kiện xóa tag
tagsContainer.addEventListener('click', function (e) {
    if (e.target.classList.contains('tag-remove')) {
        const id = e.target.dataset.id;

        // Xóa khỏi mảng
        selectedParticipants = selectedParticipants.filter(p => p !== id);
        updateHiddenInput(); // Cập nhật input hidden ngay

        // Xóa khỏi giao diện
        e.target.parentElement.remove();
    } else if (e.target === tagsContainer) {
        participantInput.focus();
    }
});

// Click ra ngoài thì đóng gợi ý
document.addEventListener('click', function (e) {
    if (!participantSuggestions.contains(e.target) && e.target !== participantInput) {
        participantSuggestions.classList.remove('active');
    }
});

// --- LOGIC QUAN TRỌNG: CẬP NHẬT INPUT HIDDEN ---
function updateHiddenInput() {
    // Chuyển mảng ['1', '5'] thành chuỗi "1,5" để Java đọc được
    hiddenInput.value = selectedParticipants.join(',');
    console.log("Current Participants IDs:", hiddenInput.value); // Debug log
}

// --- LOGIC SUBMIT FORM ---
form.addEventListener('submit', function (e) {
    // Đảm bảo lần cuối input hidden đã có dữ liệu
    updateHiddenInput();

    // Nếu muốn validate gì thêm thì làm ở đây
    // Ví dụ: Bắt buộc phải có ít nhất 1 participant? (Tuỳ logic)

    // Form sẽ tự động submit về Controller (do không có e.preventDefault())
});

// --- AUTO FILL DATE TIME (Tiện ích) ---
const now = new Date();
const dateInput = document.querySelector('input[name="date"]');
const timeInput = document.querySelector('input[name="time"]');
if (!dateInput.value) {
    dateInput.value = now.toISOString().split('T')[0];
    timeInput.value = now.toTimeString().slice(0, 5);
}

function filterRelatedTo() {
    // 1. Lấy ID khách hàng đang được chọn (Ví dụ: "1")
    var customerId = document.getElementById("customerSelect").value;

    // 2. Lấy danh sách các options trong ô Related To
    var relatedSelect = document.getElementById("relatedSelect");
    var options = relatedSelect.querySelectorAll("option");

    // 3. Reset giá trị về rỗng để tránh chọn nhầm cái đang bị ẩn
    relatedSelect.value = "";

    // 4. Duyệt qua từng option để ẩn/hiện
    options.forEach(function (opt) {
        // Luôn hiện option mặc định "-- Không liên kết --"
        if (opt.value === "") {
            opt.style.display = "block";
            return;
        }

        // Lấy customer ID được gắn trên option đó
        var dataCust = opt.getAttribute("data-customer");

        // Logic lọc:
        // - Nếu chưa chọn khách (customerId rỗng) -> Ẩn hết (hoặc hiện hết tùy bạn, nhưng nên ẩn cho gọn)
        // - Nếu có chọn khách -> Chỉ hiện option nào khớp ID
        if (customerId && dataCust === customerId) {
            opt.style.display = "block"; // Hiện
        } else {
            opt.style.display = "none";  // Ẩn
        }
    });
}

// Gọi 1 lần lúc trang vừa load để ẩn hết đi (vì lúc đầu chưa chọn khách)
document.addEventListener("DOMContentLoaded", function () {
    filterRelatedTo();
});