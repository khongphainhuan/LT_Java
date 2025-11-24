// Service Management - Enhanced logging
document.addEventListener('DOMContentLoaded', function () {
    console.log('🔧 Services page loaded');
    loadServices();
});

async function loadServices() {
    console.log('📡 Fetching services from /api/services...');
    try {
        const response = await fetch('/api/services', {
            credentials: 'include'
        });

        console.log('📊 Response status:', response.status);

        if (response.ok) {
            const services = await response.json();
            console.log('✅ Services loaded:', services.length, services);
            displayServices(services);
        } else {
            console.error('❌ Failed to load services, status:', response.status);
            const errorText = await response.text();
            console.error('Error details:', errorText);
        }
    } catch (error) {
        console.error('❌ Error loading services:', error);
    }
}

function displayServices(services) {
    const tbody = document.querySelector('tbody');

    if (!services || services.length === 0) {
        console.warn('⚠️ No services to display');
        tbody.innerHTML = `
          <tr>
            <td colspan="5" class="text-center py-5">
              <div class="text-muted">
                <i class="fas fa-clipboard-list fa-3x mb-3 opacity-25"></i>
                <p>Chưa có dịch vụ nào trong hệ thống</p>
              </div>
            </td>
          </tr>`;
        return;
    }

    console.log('🎨 Rendering', services.length, 'services');
    tbody.innerHTML = services.map(service => `
        <tr>
          <td class="fw-bold text-primary">#${service.code}</td>
          <td class="fw-medium">${service.name}</td>
          <td><span class="text-truncate d-inline-block" style="max-width: 250px;">${service.description || ''}</span></td>
          <td><span class="badge bg-success-subtle text-success">Hoạt động</span></td>
          <td class="text-end">
            <a href="/admin/service-form?id=${service.id}" class="btn-action text-primary" title="Chỉnh sửa"><i class="fas fa-edit"></i></a>
            <button class="btn-action text-danger" onclick="deleteService(${service.id})" title="Xóa"><i class="fas fa-trash"></i></button>
          </td>
        </tr>
      `).join('');
}

async function deleteService(id) {
    if (!confirm('Bạn có chắc muốn xóa dịch vụ này?')) {
        return;
    }

    console.log('🗑️ Deleting service ID:', id);

    try {
        const response = await fetch(`/api/services/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (response.ok) {
            console.log('✅ Service deleted successfully');
            alert('Xóa dịch vụ thành công!');
            loadServices(); // Reload list
        } else {
            console.error('❌ Delete failed, status:', response.status);
            alert('Lỗi khi xóa dịch vụ!');
        }
    } catch (error) {
        console.error('❌ Error deleting service:', error);
        alert('Có lỗi xảy ra!');
    }
}
