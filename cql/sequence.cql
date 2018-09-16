CREATE KEYSPACE sequence
WITH durable_writes = true
AND replication = {
    'class' : 'NetworkTopologyStrategy',
    'datacenter1' : 3
};

CREATE TABLE sequence.sequence_gen (
    name text,
    sequence bigint,
    PRIMARY KEY (name)
);

insert into sequence.sequence_gen (name, sequence) values('id', 0);