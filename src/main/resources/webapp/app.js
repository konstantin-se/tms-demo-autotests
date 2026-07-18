const state = {
  teamId: '',
  statusFilter: 'ALL',
  sort: 'dueDate',
  myTasksOnly: false,
};

let tasks = INITIAL_TASKS.map((t) => ({ ...t }));

const STATUS_LABELS = { OPEN: 'Open', IN_PROGRESS: 'In Progress', DONE: 'Done' };
const PRIORITY_LABELS = { LOW: 'Low', MEDIUM: 'Medium', HIGH: 'High' };
const PRIORITY_ORDER = { HIGH: 0, MEDIUM: 1, LOW: 2 };

function userName(userId) {
  const user = USERS.find((u) => u.id === userId);
  return user ? user.name : null;
}

function usersForTeam(teamId) {
  return USERS.filter((u) => u.teamId === teamId);
}

function visibleTasks() {
  let list = tasks.filter((t) => t.teamId === state.teamId);
  if (state.statusFilter !== 'ALL') list = list.filter((t) => t.status === state.statusFilter);
  if (state.myTasksOnly) list = list.filter((t) => t.assigneeId === CURRENT_USER_ID);
  return [...list].sort((a, b) => state.sort === 'priority'
    ? PRIORITY_ORDER[a.priority] - PRIORITY_ORDER[b.priority]
    : a.dueDate.localeCompare(b.dueDate));
}

function rowHtml(task) {
  const assigneeName = userName(task.assigneeId) || 'Unassigned';
  const assignLabel = task.assigneeId ? 'Reassign' : 'Assign';
  const assignDisabled = task.status === 'DONE';
  const completeButton = task.status !== 'DONE'
    ? `<button type="button" class="complete-btn" data-task-id="${task.id}" aria-label="Mark ${task.title} as complete">Mark complete</button>`
    : '';
  return `
    <tr data-task-id="${task.id}">
      <td>${task.title}</td>
      <td><span class="status-badge status-${task.status}">${STATUS_LABELS[task.status]}</span></td>
      <td>${PRIORITY_LABELS[task.priority]}</td>
      <td>${task.dueDate}</td>
      <td class="assignee-cell">${assigneeName}</td>
      <td class="actions-cell">
        <button type="button" class="view-btn" data-task-id="${task.id}" aria-label="View details for ${task.title}">View</button>
        <button type="button" class="assign-btn" data-task-id="${task.id}" aria-label="${assignLabel} ${task.title}" ${assignDisabled ? 'disabled' : ''}>${assignLabel}</button>
        ${completeButton}
      </td>
    </tr>
  `;
}

function render() {
  const table = document.getElementById('task-table');
  const emptyState = document.getElementById('empty-state');
  const tbody = document.getElementById('task-rows');

  if (!state.teamId) {
    table.hidden = true;
    emptyState.hidden = false;
    emptyState.textContent = 'Select a team to view its tasks.';
    tbody.innerHTML = '';
    return;
  }

  const rows = visibleTasks();
  table.hidden = false;

  if (rows.length === 0) {
    emptyState.hidden = false;
    emptyState.textContent = 'No tasks match the current filters.';
  } else {
    emptyState.hidden = true;
  }

  tbody.innerHTML = rows.map(rowHtml).join('');
}

function openAssignDialog(taskId) {
  const task = tasks.find((t) => t.id === taskId);
  const dialog = document.getElementById('assign-dialog');
  dialog.dataset.taskId = taskId;
  document.getElementById('assign-dialog-title').textContent = task.assigneeId ? 'Reassign task' : 'Assign task';
  document.getElementById('assign-dialog-task-title').textContent = task.title;

  const select = document.getElementById('assignee-select');
  select.innerHTML = '<option value="">-- Select a user --</option>' +
    usersForTeam(task.teamId).map((u) =>
      `<option value="${u.id}" ${u.id === task.assigneeId ? 'selected' : ''}>${u.name}</option>`).join('');

  document.getElementById('assign-error').hidden = true;
  dialog.showModal();
}

function confirmAssign() {
  const dialog = document.getElementById('assign-dialog');
  const taskId = dialog.dataset.taskId;
  const task = tasks.find((t) => t.id === taskId);
  if (task.status === 'DONE') {
    dialog.close();
    return;
  }
  const select = document.getElementById('assignee-select');
  if (!select.value) {
    document.getElementById('assign-error').hidden = false;
    return;
  }
  task.assigneeId = select.value;
  fetch(`/api/tasks/${taskId}/assignee`, { method: 'POST', body: task.assigneeId }).catch(() => {});
  dialog.close();
  render();
}

function openDetailDialog(taskId) {
  const task = tasks.find((t) => t.id === taskId);
  document.getElementById('detail-dialog-title').textContent = task.title;
  document.getElementById('detail-description').textContent = task.description;
  document.getElementById('detail-status').textContent = STATUS_LABELS[task.status];
  document.getElementById('detail-priority').textContent = PRIORITY_LABELS[task.priority];
  document.getElementById('detail-due-date').textContent = task.dueDate;
  document.getElementById('detail-assignee').textContent = userName(task.assigneeId) || 'Unassigned';
  document.getElementById('detail-dialog').showModal();
}

function markComplete(taskId) {
  tasks.find((t) => t.id === taskId).status = 'DONE';
  render();
}

document.getElementById('team-select').addEventListener('change', (e) => {
  state.teamId = e.target.value;
  state.myTasksOnly = false;
  document.getElementById('my-tasks-toggle').checked = false;
  render();
});

document.getElementById('status-filter').addEventListener('change', (e) => {
  state.statusFilter = e.target.value;
  render();
});

document.getElementById('sort-select').addEventListener('change', (e) => {
  state.sort = e.target.value;
  render();
});

document.getElementById('my-tasks-toggle').addEventListener('change', (e) => {
  state.myTasksOnly = e.target.checked;
  render();
});

document.getElementById('task-rows').addEventListener('click', (e) => {
  const button = e.target.closest('button');
  if (!button) return;
  const taskId = button.dataset.taskId;
  if (button.classList.contains('view-btn')) openDetailDialog(taskId);
  if (button.classList.contains('assign-btn')) openAssignDialog(taskId);
  if (button.classList.contains('complete-btn')) markComplete(taskId);
});

document.getElementById('assign-confirm-btn').addEventListener('click', confirmAssign);
document.getElementById('assign-cancel-btn').addEventListener('click', () => document.getElementById('assign-dialog').close());
document.getElementById('detail-close-btn').addEventListener('click', () => document.getElementById('detail-dialog').close());

render();
