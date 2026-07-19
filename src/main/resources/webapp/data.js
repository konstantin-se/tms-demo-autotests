const TEAMS = [
  { id: 'team-falcon', name: 'Team Falcon' },
  { id: 'team-orbit', name: 'Team Orbit' },
  { id: 'team-nova', name: 'Team Nova' },
];

// Users are no longer hardcoded here — app.js loads them from GET /api/users,
// which the gateway serves from the backend's 'users' table via gRPC ListUsers.

const CURRENT_USER_ID = 'u-ava';

const INITIAL_TASKS = [
  { id: 't-1', teamId: 'team-falcon', title: 'Investigate flaky login', description: 'Login intermittently fails on retry.', status: 'OPEN', priority: 'HIGH', dueDate: '2026-07-14', assigneeId: null },
  { id: 't-2', teamId: 'team-falcon', title: 'Refresh dashboard styles', description: 'Update the dashboard cards to the new palette.', status: 'OPEN', priority: 'LOW', dueDate: '2026-07-20', assigneeId: null },
  { id: 't-3', teamId: 'team-falcon', title: 'Write onboarding checklist', description: 'Draft the checklist for new hires.', status: 'IN_PROGRESS', priority: 'MEDIUM', dueDate: '2026-07-16', assigneeId: 'u-ava' },
  { id: 't-4', teamId: 'team-falcon', title: 'Patch export timeout', description: 'Large exports time out after 30s.', status: 'IN_PROGRESS', priority: 'HIGH', dueDate: '2026-07-12', assigneeId: 'u-marcus' },
  { id: 't-5', teamId: 'team-falcon', title: 'Archive Q1 reports', description: 'Move Q1 reports to cold storage.', status: 'DONE', priority: 'LOW', dueDate: '2026-06-30', assigneeId: 'u-priya' },
  { id: 't-6', teamId: 'team-orbit', title: 'Rotate API credentials', description: 'Quarterly credential rotation.', status: 'OPEN', priority: 'HIGH', dueDate: '2026-07-15', assigneeId: null },
  { id: 't-7', teamId: 'team-orbit', title: 'Draft release notes', description: "Summarize this sprint's changes.", status: 'IN_PROGRESS', priority: 'MEDIUM', dueDate: '2026-07-18', assigneeId: 'u-sofia' },
  { id: 't-8', teamId: 'team-orbit', title: 'Retire legacy webhook', description: 'Remove the deprecated webhook endpoint.', status: 'DONE', priority: 'MEDIUM', dueDate: '2026-06-25', assigneeId: 'u-diego' },
  { id: 't-9', teamId: 'team-nova', title: 'Tune search relevance', description: 'Improve ranking for partial matches.', status: 'OPEN', priority: 'MEDIUM', dueDate: '2026-07-22', assigneeId: null },
  { id: 't-10', teamId: 'team-nova', title: 'Fix pagination off-by-one', description: 'Last page drops the final row.', status: 'OPEN', priority: 'HIGH', dueDate: '2026-07-13', assigneeId: 'u-liam' },
];
