-- Operazione 1: Inserire un nuovo tesserato
-- Inserire un nuovo tesserato

INSERT INTO Tesserato(CF, cognome, nome, dataNascita)
VALUES ('AAABBB00C00D000E', 'CCC', 'NNN', '2000-01-01');
-- Specifichiamo il ruolo
INSERT INTO Atleta (CF, pagamentoFinoAl, idoneoFinoAl)
VALUES ('AAABBB00C00D000E', NULL, NULL);
-- In alternativa INSERT INTO Presidente/Dirigente/Allenatore

-- Operazione 2: Elimina un tesserato
DELETE FROM Tesserato
WHERE CF = 'AAABBB00C00D000E';

-- Operazione 3: Elenco degli atleti
SELECT T.CF,
       T.cognome, T.nome, T.dataNascita
FROM
    Tesserato AS T
        JOIN
    Atleta AS A ON T.CF = A.CF
ORDER BY
    T.cognome ASC,
    T.nome ASC,
    T.dataNascita DESC;

-- Operazione 4: Elenco dello staff
SELECT
    T.CF,
    T.cognome,
    T.nome,
    T.dataNascita
FROM
    Tesserato AS T
WHERE
    T.CF NOT IN (SELECT CF FROM Atleta)
ORDER BY
    T.cognome ASC, T.nome ASC, T.dataNascita ASC;

-- Operazione 5: Registrare una visita medica
INSERT INTO VisitaMedica (CF_Tesserato, ID, data, idoneoFinoAl, note)
VALUES (
'AAABBB00C00D000E',
(SELECT COALESCE(MAX(ID), 0) + 1 FROM VisitaMedica AS temp WHERE CF_Tesserato = 'AAABBB00C00D000E'),
'2024-09-03',
'2025-09-03',
DEFAULT
);
-- Aggiorno valore ridondante
UPDATE Atleta
SET idoneoFinoAl = '2025-09-03'
WHERE CF = 'AAABBB00C00D000E';
/*Poiché all’inizio un atleta può avere NULL visite mediche, COALESCE(val1, val2..) trova il primo valore non nullo.
Quando MAX(ID) è NULL seleziona 1 per inserire la prima visita.*/


-- Operazione 6: Report atleti con visita medica scaduta o in scadenza
SELECT
    T.CF, T.cognome, T.nome,
    A.idoneoFinoAl,
    VM.note
FROM
    Atleta AS A
        JOIN
    Tesserato AS T ON A.CF = T.CF
        LEFT JOIN
    VisitaMedica AS VM ON A.CF = VM.CF_Tesserato AND VM.idoneoFinoAl = A.idoneoFinoAl
WHERE
    A.idoneoFinoAl IS NULL
   OR A.idoneoFinoAl < CURDATE() -- scaduta
   OR (A.idoneoFinoAl BETWEEN CURDATE() AND CURDATE() + INTERVAL 1 MONTH) -- in scadenza
ORDER BY
    T.cognome ASC, T.nome ASC;

-- Operazione 7: Report visite mediche di un atleta
SELECT
    VM.ID, VM.data, VM.idoneoFinoAl, VM.note
FROM
    VisitaMedica AS VM
WHERE
    VM.CF_Tesserato = 'AAABBB00C00D000E'
ORDER BY VM.data ASC;
-- Operazione 8: Registrare un nuovo pagamento
INSERT INTO Pagamento (CF, data, pagamentoFinoAl, importo)
VALUES (
'AAABBB00C00D000E', '2025-02-11', '2025-05-11', 100.00);
           
           
-- Aggiorniamo il valore ridondante
UPDATE Atleta
SET pagamentoFinoAl = '2025-05-11'
WHERE CF = 'AAABBB00C00D000E';
-- Operazione 9: Report degli atleti con pagamenti irregolari
SELECT
    T.CF, T.nome, T.cognome, T.dataNascita, A.pagamentoFinoAl
FROM Atleta AS A
         JOIN
     Tesserato AS T ON A.CF = T.CF
WHERE
    A.pagamentoFinoAl < CURDATE()
   OR A.pagamentoFinoAl IS NULL
ORDER BY
    T.cognome ASC, T.nome ASC, T.dataNascita ASC;
-- Operazione 10: Report pagamenti di un atleta
SELECT
    P.ID, P.data, P.pagamentoFinoAl, P.importo
FROM
    Pagamento AS P
WHERE
    P.CF_Tesserato = 'AAABBB00C00D000E'
ORDER BY
    P.data DESC;

-- Operazione 11: Inserisci atleta in un gruppo allenamento
INSERT INTO AssegnatoA (CF_Atleta, nomeGruppo)
VALUES ('AAABBB00C00D000E', 'Pulcini');
/* Per rimuovere
DELETE FROM AssegnatoA WHERE CF_Atleta = 'AAABBB00C00D000E' AND nomeGruppo = 'Pulcini'; */
           
           
-- Operazione 12: Report atleti in un gruppo
SELECT
    T.CF,
    T.cognome,
    T.nome,
    T.dataNascita
FROM
    Tesserato AS T
        JOIN
    AssegnatoA AS AA ON T.CF = AA.CF_Atleta
WHERE
    AA.nomeGruppo = 'Pulcini'
ORDER BY
    T.cognome ASC,
    T.nome ASC,
    T.dataNascita DESC;