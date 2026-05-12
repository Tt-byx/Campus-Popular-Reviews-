export const SCORE_LABELS = {
  1: '拉完了',
  2: 'NPC',
  3: '人上人',
  4: '顶级',
  5: '夯'
}

export const SCORE_EMOJIS = {
  1: '💩',
  2: '😐',
  3: '😎',
  4: '🔥',
  5: '👑'
}

export function getScoreLabel(score) {
  if (score == null) return ''
  const rounded = Math.round(score)
  return SCORE_LABELS[rounded] || ''
}

export function getScoreLevel(score) {
  if (score == null) return 0
  if (score < 2) return 1
  if (score < 3) return 2
  if (score < 4) return 3
  if (score < 4.5) return 4
  return 5
}

export function getScoreColor(score) {
  const level = getScoreLevel(score)
  const colors = ['#ef4444', '#f97316', '#eab308', '#22c55e', '#3b82f6']
  return colors[level - 1] || '#9ca3af'
}
