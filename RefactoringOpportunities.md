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
