if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[RXS_CT_SHARED_RSS_IMPORT]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[RXS_CT_SHARED_RSS_IMPORT]
GO

CREATE TABLE [dbo].[RXS_CT_SHARED_RSS_IMPORT] (
	[CONTENTID] [int] NOT NULL ,
	[REVISIONID] [int] NOT NULL ,
	[RSS_GUID] [nvarchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
	[RSS_SYNCID] [nvarchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
	[RSS_VERSIONID] [nvarchar] (50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL 
) ON [PRIMARY]
GO

