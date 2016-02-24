# --- Sample dataset

# --- !Ups

insert into country (id, name) values
(1, 'United States'),
(44, 'United Kingdom'),
(55, 'Brazil'),
(61, 'Australia');

insert into musician (id, name, country_id) values
(200, 'Pink Floyd', 44),
(201, 'Dire Straits', 44),
(202, 'The Killers', 1),
(203, 'Sia Furler', 61),
(204, 'The Eagles', 1),
(205, 'Imagine Dragons', 1),
(206, 'Radiohead', 44),
(207, 'Elton John', 44),
(208, 'Queen', 44);

insert into song (id, name, released, musician_id) values
(1000, 'Candle In The Wind', '1973-10-05', 207),
(1001, 'Sacrifice', '1989-08-29', 207),
(1002, 'Tonight', '1976-10-12', 207),
(1003, 'Goodbye Yellow Brick Road', '1973-10-05', 207),
(1004, 'Rocket Man', '1972-04-14', 207),
(1005, 'Creep', '1994-10-24', 206),
(1006, 'Fake Plastic Trees', '1995-03-13', 206),
(1007, 'Street Spirit', '1995-03-13', 206),
(1008, 'Bohemian Rhapsody', '1975-11-21', 208),
(1009, '39', '1975-11-21', 208),
(1010, 'We Are The Champions', '1977-10-07', 208),
(1011, 'Love Of My Life', '1975-11-21', 208),
(1012, 'Wish You Were Here', '1975-09-12', 200),
(1013, 'Time', '1973-03-01', 200),
(1014, 'The Final Cut', '1983-03-21', 200),
(1015, 'Comfortably Numb', '1979-11-30', 200),
(1016, 'The Thin Ice', '1979-11-30', 200),
(1017, 'Brain Damage', '1973-03-01', 200),
(1018, 'Big Girls Cry', '2014-07-04', 203),
(1019, 'Elastic Heart', '2014-07-04', 203),
(1020, 'Alive', '2016-01-29', 203),
(1021, 'Chandelier', '2014-07-04', 203),
(1022, 'Hotel California', '1976-12-08', 204),
(1023, 'Take It Easy', '1972-06-01', 204),
(1024, 'The Last Resort', '1976-12-08', 204),
(1025, 'Human', '2008-09-22', 202),
(1026, 'A Dustland Fairytale', '2009-05-09', 202),
(1027, 'Demons', '2012-09-04', 205),
(1028, 'It Comes Back To You', '2015-02-17', 205),
(1029, 'Your Latest Trick', '1985-05-13', 201),
(1030, 'Brothers In Arms', '1985-05-13', 201),
(1031, 'Money For Nothing', '1985-05-13', 201),
(1032, 'Tunnel Of Love', '1980-10-17', 201);

# --- !Downs

delete from computer;
delete from company;
