SELECT * FROM BERATER;
select * from BERATER_AUD;
select * from REVISION;
select * from REVCHANGES;

//and r.date <= PARSEDATETIME('2016-02-18 09:06:46.539','yyyy-MM-dd hh:mm:ss.SSS');

select * from revision r where r.date > PARSEDATETIME('2016-02-18 09:06:46.539','yyyy-MM-dd hh:mm:ss.SSS');

(select max(r.id) from revision r where r.date <= PARSEDATETIME('2016-02-18 09:06:46.539','yyyy-MM-dd hh:mm:ss.SSS'));

select * from revision r where r.date <= PARSEDATETIME('2016-02-18 09:06:46.539','yyyy-MM-dd hh:mm:ss.SSS');

select * from revision;
select * from berater_aud ba where ba.rev = (select max(r.id) from revision r where r.date <= PARSEDATETIME('2016-02-18 09:06:46.539','yyyy-MM-dd hh:mm:ss.SSS')) ;

select * from berater_aud ba where ba.rev <= (select max(r.id) from revision r where r.date <= PARSEDATETIME('2016-02-18 09:06:46.539','yyyy-MM-dd hh:mm:ss.SSS')) and (ba.revend > (select max(r.id) from revision r where r.date <= PARSEDATETIME('2016-02-18 09:06:46.539','yyyy-MM-dd hh:mm:ss.SSS')) or ba.revend is null) order by ba.id asc;
select * from berater;


select * from berater_aud ba where ba.revend < (select max(r.id) from revision r where r.date <= PARSEDATETIME('2016-02-18 09:06:46.539','yyyy-MM-dd hh:mm:ss.SSS'));
select * from berater_aud;
select * from berater;
select * from berater_aud ba where ba.rev in (5,7)

-- einfache hierarchie-abfrage:
WITH RECURSIVE hq(id, name,VORGESETZTER_ID,hid, gueltig_von, gueltig_bis) AS (
select b.id, b.name,h.VORGESETZTER_ID,h.id, h.gueltig_von, h.gueltig_bis   from berater b join hierarchie h on b.id  = h.berater_id where CURRENT_TIMESTAMP between h.gueltig_von  and h.gueltig_bis and b.name = 'Juniorberater'
    UNION ALL
select vorgesetzter.id, vorgesetzter.name, h.VORGESETZTER_ID, h.id, h.gueltig_von, h.gueltig_bis from hq join berater vorgesetzter on vorgesetzter.id = hq.VORGESETZTER_ID join hierarchie h on vorgesetzter.id  = h.berater_id where CURRENT_TIMESTAMP between h.gueltig_von  and h.gueltig_bis
)
SELECT * FROM hq order by hq.hid asc; 
select * from hierarchie;

-- revision mit datum abfragen:
select max(r.id) from revision r where r.date <= PARSEDATETIME('2016-02-22 10:30:56.740','yyyy-MM-dd hh:mm:ss.SSS')
-- 2 hierarchie-abfragen mit revisionen
WITH RECURSIVE hq(id, name,VORGESETZTER_ID,hid, gueltig_von, gueltig_bis) AS (
select b.id, b.name,h.VORGESETZTER_ID,h.id, h.gueltig_von, h.gueltig_bis   from berater b join hierarchie_aud h on b.id  = h.berater_id where h.rev <= 1 and (h.revend > 1 or h.revend is null) and b.name = 'Juniorberater' and CURRENT_TIMESTAMP between h.gueltig_von  and h.gueltig_bis
    UNION ALL
select vorgesetzter.id, vorgesetzter.name, h.VORGESETZTER_ID, h.id, h.gueltig_von, h.gueltig_bis from hq join berater vorgesetzter on vorgesetzter.id = hq.VORGESETZTER_ID join hierarchie_aud h on vorgesetzter.id  = h.berater_id where h.rev <= 1 and (h.revend > 1 or h.revend is null) and CURRENT_TIMESTAMP between h.gueltig_von  and h.gueltig_bis
)
SELECT * FROM hq order by hq.hid asc; 

WITH RECURSIVE hq(id, name,VORGESETZTER_ID,hid, gueltig_von, gueltig_bis) AS (
select b.id, b.name,h.VORGESETZTER_ID,h.id, h.gueltig_von, h.gueltig_bis   from berater b join hierarchie_aud h on b.id  = h.berater_id where h.rev <= 2 and (h.revend > 2 or h.revend is null) and b.name = 'Juniorberater' and CURRENT_TIMESTAMP between h.gueltig_von  and h.gueltig_bis
    UNION ALL
select vorgesetzter.id, vorgesetzter.name, h.VORGESETZTER_ID, h.id, h.gueltig_von, h.gueltig_bis from hq join berater vorgesetzter on vorgesetzter.id = hq.VORGESETZTER_ID join hierarchie_aud h on vorgesetzter.id  = h.berater_id where h.rev <= 2 and (h.revend > 2 or h.revend is null) and CURRENT_TIMESTAMP between h.gueltig_von  and h.gueltig_bis
)
SELECT * FROM hq order by hq.hid asc; 


select * from hierarchie_aud ha where ha.rev <= (select max(r.id) from revision r where r.date <= PARSEDATETIME('2016-02-18 16:53:35.930','yyyy-MM-dd hh:mm:ss.SSS')) and (ha.revend > (select max(r.id) from revision r where r.date <= PARSEDATETIME('2016-02-18 16:53:35.930','yyyy-MM-dd hh:mm:ss.SSS')) or ha.revend is null) and ha.berater_id = 6;
select * from hierarchie_aud ha where ha.rev <= (select max(r.id) from revision r where r.date <= PARSEDATETIME('2016-02-18 16:53:41.150','yyyy-MM-dd hh:mm:ss.SSS')) and (ha.revend > (select max(r.id) from revision r where r.date <= PARSEDATETIME('2016-02-18 16:53:41.150','yyyy-MM-dd hh:mm:ss.SSS')) or ha.revend is null) and ha.berater_id = 6;
select * from revision;
select * from hierarchie h where h.berater_id  =	6;
select * from hierarchie_aud a where a.berater_id =	6;