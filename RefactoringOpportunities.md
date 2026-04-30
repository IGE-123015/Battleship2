# Refactoring Opportunities

| Local (classe::método) | Nome do Cheiro no Código | Nome da Refabricação | Nº Alun@ |
|---|---|---|---|
| `GameRecord` | Data Class | Encapsulate Field | 94334 |
| `Fleet::colisionRisk` | Duplicated Code / Feature Envy | Rename | 94334 |
| `Ship::getTopMostPos` / `getBottomMostPos` / `getLeftMostPos` / `getRightMostPos` | Duplicated Code | Extract Method | 94334 |
| `Compass::charToCompass` | Switch Statements | Replace Conditional with Polymorphism (Map lookup) | 94334 |
| `Move::processEnemyFire` | Long Method | Extract Method | 94334 |
| `Fleet::colisionRisk` | Duplicated Code | Replace Loop with Enhanced For | 94334 |
| `Tasks::menu` | Long Method / Switch Statements | Introduce Constant | 94334 |
| `PdfReport` | Magic Number / Magic Literal | Introduce Constant | 106806 |
| `Game::printBoard` | Naming (parâmetro `show_shots` viola convenção camelCase) | Rename | 106806 |
| `Game::printBoard` | Variável temporária usada uma única vez (`rowLabel`) | Inline Variable | 106806 |
| `Game::fireSingleShot` | Expressão composta difícil de ler (`isRepeated \|\| repeatedShot(pos)`) | Introduce Variable | 106806 |
| `Game::printBoard` | Long Method (método com >60 linhas) | Extract Method | 106806 |
| `Game::randomEnemyFire` | Long Method (construção de candidatos misturada com lógica de disparo) | Extract Method | 106806 |
| `Timer::getSeconds`| Expression too complex | Introduce Variable | 123015 |
| `Ship` | Public Field | Encapsulate Field | 123015 |
| `Game::fireSingleShot` | Unused Assignment | Inline Variable | 123015 |
| `Fleet` | Dead Code | Remove Commented-Out Code | 123015 |
| `Barge`| Duplicate Code | Pull Up Method | 123015 |
