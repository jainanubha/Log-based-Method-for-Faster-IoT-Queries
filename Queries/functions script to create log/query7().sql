-- Function: public.query7()

-- DROP FUNCTION public.query7();

CREATE OR REPLACE FUNCTION public.query7()
  RETURNS void AS
$BODY$DECLARE
	rec_row RECORD;
	rec_cursor CURSOR 
	FOR
	((select L1.OID as oid1,L2.OID as oid2,L3.OID as oid3,L4.OID as oid4,L5.OID as oid5,L6.OID as oid6
from "lod8" L1, "lod8" L2, "lod8" L3, "lod8" L4, "lod8" L5, "lod8" L6
where
L1.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#RainfallObservation>'
and L1.pred = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>'
and L2.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#result>'
and L2.sub = L1.sub
and L3.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#uom>'
and L3.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#centimeters>'
and L3.sub = L2.obj
and L4.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#floatValue>'
and L4.sub = L3.sub
and L6.obj like '"2004-08-12%'
and L6.pred = '<http://www.w3.org/2006/time#inXSDDateTime>'
and L5.obj = L6.sub
and L5.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#samplingTime>'
and L5.sub = L1.sub)
union
(select T1.OID as oid1,T2.OID as oid2,T3.OID as oid3,T4.OID as oid4,T5.OID as oid5,T6.OID as oid6
from "lod8" T1, "lod8" T2, "lod8" T3, "lod8" T4, "lod8" T5, "lod8" T6
where
T1.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#RainfallObservation>'
and T1.pred = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>'
and T2.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#result>'
and T2.sub = T1.sub
and T3.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#uom>'
and T3.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#centimeters>'
and T3.sub = T2.obj
and T4.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#floatValue>'
and T4.sub = T3.sub
and T6.obj like '"2004-08-13%'
and T6.pred = '<http://www.w3.org/2006/time#inXSDDateTime>'
and T5.obj = T6.sub
and T5.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#samplingTime>'
and T5.sub = T1.sub));

BEGIN
	OPEN rec_cursor;
	LOOP
		FETCH rec_cursor INTO rec_row;
		EXIT WHEN NOT FOUND;
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid1,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid2,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid3,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid4,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid5,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid6,clock_timestamp());
	END LOOP;
CLOSE rec_cursor;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.query7()
  OWNER TO postgres;
