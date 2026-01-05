CREATE SCHEMA ScuolaCalcio;
USE ScuolaCalcio;


-- per una rapida implementazione, sono stati omessi alcuni attributi indicati nel file di progetto

CREATE TABLE Tesserato (
                           CF VARCHAR(16) PRIMARY KEY CHECK (CHAR_LENGTH(CF) = 16),
                           cognome VARCHAR(50) NOT NULL,
                           nome VARCHAR(50) NOT NULL,
                           dataNascita DATE NOT NULL
);
CREATE TABLE Presidente (
                            CF VARCHAR(16) PRIMARY KEY NOT NULL,
                            FOREIGN KEY (CF) REFERENCES Tesserato(CF) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE TABLE Dirigente (
                           CF VARCHAR(16) PRIMARY KEY NOT NULL,
                           FOREIGN KEY (CF) REFERENCES Tesserato(CF) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE TABLE Allenatore (
                            CF VARCHAR(16) PRIMARY KEY NOT NULL,
                            FOREIGN KEY (CF) REFERENCES Tesserato(CF) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE TABLE Atleta (
                        CF VARCHAR(16) PRIMARY KEY,
                        pagamentoFinoAl DATE,
                        idoneoFinoAl DATE,
                        FOREIGN KEY (CF) REFERENCES Tesserato(CF) ON UPDATE CASCADE ON DELETE CASCADE);

CREATE TABLE VisitaMedica (
                              CF_Tesserato CHAR(16) NOT NULL,
                              ID INT NOT NULL,
                              data DATE NOT NULL,
                              idoneoFinoAl DATE NOT NULL,
                              note VARCHAR(100) DEFAULT '',
                              PRIMARY KEY (ID, CF_Tesserato),
                              FOREIGN KEY (CF_Tesserato) REFERENCES Atleta(CF) ON DELETE CASCADE
);
CREATE TABLE Pagamento (
                           ID INT AUTO_INCREMENT PRIMARY KEY,
                           CF VARCHAR(16) NOT NULL, --indipendente
                           data DATE NOT NULL,
                           pagamentoFinoAl DATE NOT NULL,
                           importo FLOAT NOT NULL
);
CREATE TABLE GruppoAllenamento (
                                   nome VARCHAR(50) PRIMARY KEY NOT NULL
);

CREATE TABLE AssegnatoA (
                            CF_Atleta VARCHAR(16) PRIMARY KEY,
                            nomeGruppo VARCHAR(50) NOT NULL,
                            FOREIGN KEY (CF_Atleta) REFERENCES Atleta(CF) ON UPDATE CASCADE ON DELETE CASCADE,
                            FOREIGN KEY (nomeGruppo) REFERENCES GruppoAllenamento(nome) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE TABLE Allena (
                        CF_Allenatore VARCHAR(16) NOT NULL,
                        nomeGruppo VARCHAR(50) NOT NULL,
                        PRIMARY KEY (CF_Allenatore, nomeGruppo),
                        FOREIGN KEY (CF_Allenatore) REFERENCES Allenatore(CF) ON UPDATE CASCADE ON DELETE CASCADE,
                        FOREIGN KEY (nomeGruppo) REFERENCES GruppoAllenamento(nome) ON UPDATE CASCADE ON DELETE CASCADE);

