# Refactoring Opportunities

| Local | Nome do Cheiro no Código | Nome da Refabricação | Nº Aluno |
|---|---|---|---|
| `PdfReport` | Magic Number / Magic Literal | Introduce Constant | 106806 |
| `Game::printBoard` | Naming (parâmetro `show_shots` viola convenção camelCase) | Rename | 106806 |
| `Game::printBoard` | Variável temporária usada uma única vez (`rowLabel`) | Inline Variable | 106806 |
| `Game::fireSingleShot` | Expressão composta difícil de ler (`isRepeated \|\| repeatedShot(pos)`) | Introduce Variable | 106806 |
| `Game::printBoard` | Long Method (método com >60 linhas) | Extract Method | 106806 |
| `Game::randomEnemyFire` | Long Method (construção de candidatos misturada com lógica de disparo) | Extract Method | 106806 |
