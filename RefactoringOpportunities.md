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
| `Position::adjacentPositions` | Magic Array / Duplicated Initialization | Extract Constant | 112949 |
| `Ship::tooCloseTo(IShip)` | Verbose Iterator Loop | Replace Loop with Enhanced For | 112949 |
| `Move::processEnemyFire` | Long Method | Extract Method (buildJsonResponse) | 112949 |
| `Fleet::addShip` | Unnecessary Local Variable | Inline Variable | 112949 |
| `Position::equals` | Verbose instanceof + cast | Replace with Pattern Matching for instanceof | 112949 |
| `Timer::getTimeMillis` | Expression too complex | Introduce Variable | 123015 |
| `Position::isNeighbor` | Long Method | Extract Method | 123015 |
| `Ship` (variáveis de instância) | Public Field | Encapsulate Field | 123015 |
| `Barge` (construtor) | Duplicate Code | Pull Up Method | 123015 |
| `Carrack` | Unused Declaration | Safe Delete | 123015 |
