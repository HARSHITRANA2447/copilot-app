// app.js - Task management frontend

(function () {
  const apiBase = '/api/tasks';

  // DOM elements
  const taskListEl = document.getElementById('taskList');
  const emptyStateEl = document.getElementById('emptyState');
  const taskForm = document.getElementById('taskForm');
  const titleInput = document.getElementById('titleInput');
  const descInput = document.getElementById('descInput');
  const completedInput = document.getElementById('completedInput');
  const saveBtn = document.getElementById('saveBtn');
  const cancelEdit = document.getElementById('cancelEdit');
  const formTitle = document.getElementById('formTitle');
  const formMessage = document.getElementById('formMessage');
  const searchInput = document.getElementById('searchInput');
  const filterCompleted = document.getElementById('filterCompleted');
  const refreshBtn = document.getElementById('refreshBtn');
  const toggleThemeBtn = document.getElementById('toggleTheme');

  let editingId = null;
  let tasksCache = [];

  // ---------------- API helpers ----------------
  async function apiGet(path = '') {
    const res = await fetch(apiBase + path, {
      headers: { 'Accept': 'application/json' }
    });
    if (!res.ok) throw new Error(`API GET ${path} failed: ${res.status}`);
    return res.json();
  }

  async function apiPost(path = '', body = {}) {
    const res = await fetch(apiBase + path, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    if (!res.ok) {
      const text = await res.text();
      throw new Error(text || `API POST ${path} failed: ${res.status}`);
    }
    return res.json();
  }

  async function apiPut(path = '', body = {}) {
    const res = await fetch(apiBase + path, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    if (!res.ok) {
      const text = await res.text();
      throw new Error(text || `API PUT ${path} failed: ${res.status}`);
    }
    return res.json();
  }

  async function apiDelete(path = '') {
    const res = await fetch(apiBase + path, { method: 'DELETE' });
    if (!res.ok) throw new Error(`API DELETE ${path} failed: ${res.status}`);
    return true;
  }

  // ---------------- Rendering ----------------
  function renderTasks(list) {
    taskListEl.innerHTML = '';
    tasksCache = list || [];

    if (!tasksCache.length) {
      emptyStateEl.style.display = '';
      return;
    }
    emptyStateEl.style.display = 'none';

    tasksCache.forEach(task => {
      const card = document.createElement('div');
      card.className = 'task-card fade-in' + (task.completed ? ' completed' : '');

      const meta = document.createElement('div');
      meta.className = 'meta';

      const title = document.createElement('h4');
      title.className = 'task-title';
      title.textContent = task.title || '(no title)';

      const desc = document.createElement('p');
      desc.className = 'task-desc';
      desc.textContent = task.description || '';

      const footer = document.createElement('div');
      footer.className = 'row';
      const ts = document.createElement('span');
      ts.className = 'timestamp';
      ts.textContent = task.createdAt ? new Date(task.createdAt).toLocaleString() : '';
      footer.appendChild(ts);

      meta.appendChild(title);
      meta.appendChild(desc);
      meta.appendChild(footer);

      const actions = document.createElement('div');
      actions.className = 'task-actions';

      const editBtn = document.createElement('button');
      editBtn.className = 'icon-btn';
      editBtn.title = 'Edit';
      editBtn.textContent = '✏️';
      editBtn.addEventListener('click', () => startEdit(task));

      const delBtn = document.createElement('button');
      delBtn.className = 'icon-btn';
      delBtn.title = 'Delete';
      delBtn.textContent = '🗑️';
      delBtn.addEventListener('click', async () => {
        if (!confirm('Delete this task?')) return;
        try {
          await apiDelete('/' + task.id);
          await refresh();
        } catch (err) {
          alert('Delete failed: ' + err.message);
        }
      });

      const completeBtn = document.createElement('button');
      completeBtn.className = 'icon-btn';
      completeBtn.title = task.completed ? 'Mark as incomplete' : 'Mark as complete';
      completeBtn.textContent = task.completed ? '↩️' : '✅';
      completeBtn.addEventListener('click', async () => {
        try {
          await apiPost('/' + task.id + '/complete');
          await refresh();
        } catch (err) {
          alert('Complete failed: ' + err.message);
        }
      });

      actions.appendChild(completeBtn);
      actions.appendChild(editBtn);
      actions.appendChild(delBtn);

      card.appendChild(meta);
      card.appendChild(actions);
      taskListEl.appendChild(card);
    });
  }

  // ---------------- Form handling ----------------
  function resetForm() {
    editingId = null;
    formTitle.textContent = 'Create Task';
    formMessage.textContent = '';
    taskForm.reset();
  }

  function startEdit(task) {
    editingId = task.id;
    formTitle.textContent = 'Edit Task';
    titleInput.value = task.title || '';
    descInput.value = task.description || '';
    completedInput.checked = !!task.completed;
    formMessage.textContent = '';
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  taskForm.addEventListener('submit', async function (e) {
    e.preventDefault();
    const payload = {
      title: titleInput.value.trim(),
      description: descInput.value.trim(),
      completed: completedInput.checked
    };
    try {
      if (!payload.title) {
        formMessage.textContent = 'Title is required';
        return;
      }
      saveBtn.disabled = true;
      if (editingId) {
        await apiPut('/' + editingId, payload);
        formMessage.textContent = 'Updated';
      } else {
        await apiPost('', payload);
        formMessage.textContent = 'Created';
      }
      resetForm();
      await refresh();
    } catch (err) {
      formMessage.textContent = 'Error: ' + (err.message || err);
    } finally {
      saveBtn.disabled = false;
    }
  });

  cancelEdit.addEventListener('click', function () { resetForm(); });

  // ---------------- Filters & search ----------------
  function applyFilters() {
    const q = (searchInput.value || '').trim().toLowerCase();
    const fc = filterCompleted.value;
    let filtered = tasksCache.slice();
    if (fc === 'true' || fc === 'false') {
      filtered = filtered.filter(t => String(t.completed) === fc);
    }
    if (q) {
      filtered = filtered.filter(t => (t.title || '').toLowerCase().includes(q));
    }
    renderTasks(filtered);
  }

  searchInput.addEventListener('input', debounce(applyFilters, 250));
  filterCompleted.addEventListener('change', applyFilters);

  // ---------------- Refresh & boot ----------------
  async function refresh() {
    try {
      const list = await apiGet('');
      tasksCache = list || [];
      applyFilters();
    } catch (err) {
      taskListEl.innerHTML = '<div class="empty">Failed to load tasks: ' + (err.message || '') + '</div>';
    }
  }

  refreshBtn.addEventListener('click', refresh);

  toggleThemeBtn.addEventListener('click', () => {
    document.body.classList.toggle('dark');
  });

  // ---------------- Utils ----------------
  function debounce(fn, ms) {
    let t = null;
    return function (...args) {
      clearTimeout(t);
      t = setTimeout(() => fn.apply(this, args), ms);
    };
  }

  // Initial load
  document.addEventListener('DOMContentLoaded', () => {
    resetForm();
    refresh();
  });

})();
