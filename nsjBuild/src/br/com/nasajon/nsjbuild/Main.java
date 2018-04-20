package br.com.nasajon.nsjbuild;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.xml.bind.JAXBException;

import br.com.nasajon.nsjbuild.controller.Compilador;
import br.com.nasajon.nsjbuild.delphi.InterpretadorDproj;
import br.com.nasajon.nsjbuild.delphi.Unit;
import br.com.nasajon.nsjbuild.exception.DependenciaInvalidaException;
import br.com.nasajon.nsjbuild.exception.GrafoCiclicoException;
import br.com.nasajon.nsjbuild.exception.ProjectFileNotFoundException;
import br.com.nasajon.nsjbuild.model.BuildMode;
import br.com.nasajon.nsjbuild.model.BuildTarget;
import br.com.nasajon.nsjbuild.model.Grafo;
import br.com.nasajon.nsjbuild.model.No;
import br.com.nasajon.nsjbuild.model.ProjetoWrapper;
import br.com.nasajon.nsjbuild.modelXML.Projeto;
import br.com.nasajon.nsjbuild.modelXML.buildParameters.ParametrosNsjbuild;
import br.com.nasajon.nsjbuild.util.XMLHandler;

public class Main {
	private static final String PAR_HELP = "/? -? --help -help /help";
	
	private static final String PAR_BUILD_CLEAN = "clean";
//	private static final String PAR_BUILD_CLEAN_CACHE = "clean_cache";
	private static final String PAR_BUILD_UPDATE = "update";
//	private static final String PAR_BUILD_ALTERADOS = "alterados";
//	private static final String PAR_BUILD_FORCE = "force";
	private static final String PAR_BUILD_VALIDATE = "validate";

	private static final String PADRAO_UNIT_DRP =  "<NOME_UNIT> in '<CAMINHO_UNIT>'";
	private static final String PADRAO_FROM_DRP =  "<NOME_UNIT> in '<CAMINHO_UNIT>' {<INSTANCIA_FRAME>: <TIPO_FRAME>}";

	private static final String PADRAO_UNIT_DPROJ = "<DCCReference Include=\"<CAMINHO_UNIT>\"/>";
	private static final String PADRAO_FROM_DPROJ = "        <DCCReference Include=\"<CAMINHO_UNIT>\">\r\n" + 
													"            <Form><INSTANCIA_FRAME></Form>\r\n" + 
													"            <DesignClass><TIPO_FRAME></DesignClass>\r\n" + 
													"        </DCCReference>\r\n"; 
	
	private static final String DEFAULT_DPR = "program default;\r\n" + 
			"\r\n" + 
			"uses\r\n" + 
			"  Vcl.Forms\r\n" + 
			"  <UNITS>\r\n" + 
			"  ;\r\n" + 
			"  \r\n" + 
			"\r\n" + 
			"{$R *.res}\r\n" + 
			"\r\n" + 
			"begin\r\n" + 
			"  Application.Initialize;\r\n" + 
			"  Application.MainFormOnTaskbar := True;\r\n" + 
			"  Application.Run;\r\n" + 
			"end.\r\n" + 
			"";
	
	private static final String DEFAULT_DPROJ = "<Project xmlns=\"http://schemas.microsoft.com/developer/msbuild/2003\">\r\n" + 
			"    <PropertyGroup>\r\n" + 
			"        <ProjectGuid>{D5EFC446-48E8-43E7-B1AA-C801083556F0}</ProjectGuid>\r\n" + 
			"        <ProjectVersion>18.2</ProjectVersion>\r\n" + 
			"        <FrameworkType>VCL</FrameworkType>\r\n" + 
			"        <MainSource>default.dpr</MainSource>\r\n" + 
			"        <Base>True</Base>\r\n" + 
			"        <Config Condition=\"'$(Config)'==''\">Debug</Config>\r\n" + 
			"        <Platform Condition=\"'$(Platform)'==''\">Win32</Platform>\r\n" + 
			"        <TargetedPlatforms>1</TargetedPlatforms>\r\n" + 
			"        <AppType>Application</AppType>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"'$(Config)'=='Base' or '$(Base)'!=''\">\r\n" + 
			"        <Base>true</Base>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"('$(Platform)'=='Win32' and '$(Base)'=='true') or '$(Base_Win32)'!=''\">\r\n" + 
			"        <Base_Win32>true</Base_Win32>\r\n" + 
			"        <CfgParent>Base</CfgParent>\r\n" + 
			"        <Base>true</Base>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"('$(Platform)'=='Win64' and '$(Base)'=='true') or '$(Base_Win64)'!=''\">\r\n" + 
			"        <Base_Win64>true</Base_Win64>\r\n" + 
			"        <CfgParent>Base</CfgParent>\r\n" + 
			"        <Base>true</Base>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"'$(Config)'=='Debug' or '$(Cfg_1)'!=''\">\r\n" + 
			"        <Cfg_1>true</Cfg_1>\r\n" + 
			"        <CfgParent>Base</CfgParent>\r\n" + 
			"        <Base>true</Base>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"('$(Platform)'=='Win32' and '$(Cfg_1)'=='true') or '$(Cfg_1_Win32)'!=''\">\r\n" + 
			"        <Cfg_1_Win32>true</Cfg_1_Win32>\r\n" + 
			"        <CfgParent>Cfg_1</CfgParent>\r\n" + 
			"        <Cfg_1>true</Cfg_1>\r\n" + 
			"        <Base>true</Base>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"'$(Config)'=='Release' or '$(Cfg_2)'!=''\">\r\n" + 
			"        <Cfg_2>true</Cfg_2>\r\n" + 
			"        <CfgParent>Base</CfgParent>\r\n" + 
			"        <Base>true</Base>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"('$(Platform)'=='Win32' and '$(Cfg_2)'=='true') or '$(Cfg_2_Win32)'!=''\">\r\n" + 
			"        <Cfg_2_Win32>true</Cfg_2_Win32>\r\n" + 
			"        <CfgParent>Cfg_2</CfgParent>\r\n" + 
			"        <Cfg_2>true</Cfg_2>\r\n" + 
			"        <Base>true</Base>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"'$(Base)'!=''\">\r\n" + 
			"        <DCC_UnitSearchPath>..\\..\\..\\_codelink\\components\\eureka;..\\..\\..\\_codelink\\components\\jedi;..\\..\\..\\_codelink\\components\\unidac;..\\..\\..\\_codelink\\components\\devexpress;..\\..\\..\\_codelink\\commonfeature\\docEngine;..\\..\\..\\_codelink\\commonfeature;..\\..\\..\\_codelink\\commonutils_static;..\\..\\..\\_codelink\\components\\skin;..\\..\\..\\_codelink\\frameworks;..\\..\\..\\_codelink\\libraries;..\\..\\..\\_codelink\\components\\nsReport;..\\..\\..\\_codelink\\components\\nsCompXE;..\\..\\..\\_codelink\\api;..\\..\\..\\_codelink\\components\\rbuilder;..\\..\\..\\_codelink\\components\\acbr;$(DCC_UnitSearchPath)</DCC_UnitSearchPath>\r\n" + 
			"        <UWP_DelphiLogo44>$(BDS)\\bin\\Artwork\\Windows\\UWP\\delphi_UwpDefault_44.png</UWP_DelphiLogo44>\r\n" + 
			"        <UWP_DelphiLogo150>$(BDS)\\bin\\Artwork\\Windows\\UWP\\delphi_UwpDefault_150.png</UWP_DelphiLogo150>\r\n" + 
			"        <SanitizedProjectName>default</SanitizedProjectName>\r\n" + 
			"        <Icon_MainIcon>$(BDS)\\bin\\delphi_PROJECTICON.ico</Icon_MainIcon>\r\n" + 
			"        <DCC_Namespace>System;Xml;Data;Datasnap;Web;Soap;Vcl;Vcl.Imaging;Vcl.Touch;Vcl.Samples;Vcl.Shell;$(DCC_Namespace)</DCC_Namespace>\r\n" + 
			"        <VerInfo_Keys>CompanyName=;FileDescription=$(MSBuildProjectName);FileVersion=1.0.0.0;InternalName=;LegalCopyright=;LegalTrademarks=;OriginalFilename=;ProgramID=com.embarcadero.$(MSBuildProjectName);ProductName=$(MSBuildProjectName);ProductVersion=1.0.0.0;Comments=</VerInfo_Keys>\r\n" + 
			"        <VerInfo_Locale>1046</VerInfo_Locale>\r\n" + 
			"        <DCC_DcuOutput>..\\..\\..\\_codelink\\commonfeature</DCC_DcuOutput>\r\n" + 
			"        <DCC_ExeOutput>$(nsbin)</DCC_ExeOutput>\r\n" + 
			"        <DCC_E>false</DCC_E>\r\n" + 
			"        <DCC_N>false</DCC_N>\r\n" + 
			"        <DCC_S>false</DCC_S>\r\n" + 
			"        <DCC_F>false</DCC_F>\r\n" + 
			"        <DCC_K>false</DCC_K>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"'$(Base_Win32)'!=''\">\r\n" + 
			"        <DCC_UsePackage>DBXSqliteDriver;dxSkinBlueprintRS24;dxPSDBTeeChartRS24;vquery240;dxPSdxGaugeControlLnkRS24;vclactnband;dxSpreadSheetRS24;vclFireDAC;fs24;dxDockingRS24;rbADO1724;tethering;dxSkinVisualStudio2013BlueRS24;dxPScxTLLnkRS24;dxBarExtItemsRS24;FireDACADSDriver;dxFireDACServerModeRS24;JvPluginSystem;dxPSTeeChartRS24;dxSkinOffice2007BlackRS24;vcltouch;JvBands;ACBr_NFe;vcldb;NasajonSkinDestaqueRS24;svn;dxSkinXmas2008BlueRS24;dxSkinscxSchedulerPainterRS24;JvJans;rbIBE1724;ACBr_NFeDanfeESCPOS;dxSkinsdxBarPainterRS24;dxSkinOffice2010BlackRS24;dacfmx240;dxADOServerModeRS24;JvDotNetCtrls;dxGDIPlusRS24;dxPSdxDBTVLnkRS24;frx24;vclib;frxDBX24;dxSkinLilianRS24;crcontrols240;rbRCL1724;dxNavBarRS24;vclx;gtDocEngD24;cxTreeListRS24;dxSkinDevExpressDarkStyleRS24;dxtrmdRS24;ACBr_SATExtratoRL;rbCIDE1724;RESTBackendComponents;dxRibbonRS24;VCLRESTComponents;cxExportRS24;cxPivotGridChartRS24;cxTreeListdxBarPopupMenuRS24;dxSkinOffice2013LightGrayRS24;rbFireDAC1724;dxTabbedMDIRS24;vclie;dxSkinVisualStudio2013LightRS24;bindengine;CloudService;JvHMI;FireDACMySQLDriver;fsDB24;cxPivotGridOLAPRS24;gtRBExpD24;ACBr_PAF;ACBr_SATECFVirtual;dxSkinSharpRS24;dxSkinBlackRS24;dxPSLnksRS24;bindcompdbx;ACBr_CTeDacteRL;dxSkinCoffeeRS24;ACBr_TCP;IndyIPServer;rbRIDE1724;ACBr_CTe;fsIBX24;dxCoreRS24;IndySystem;dxSkinsdxRibbonPainterRS24;FMXfsTee24;dxSkinOffice2013DarkGrayRS24;dsnapcon;rbDB1724;ACBr_synapse;FireDACMSAccDriver;fmxFireDAC;vclimg;ACBr_SPEDImportar;Jcl;nsReportComponents;ACBr_SPED;ACBr_MDFe;dxBarDBNavRS24;dxSkinDarkSideRS24;dclRBFireDAC1724;dclRBIBE1724;ACBr_BoletoRL;ACBr_LFD;dxSkinOffice2013WhiteRS24;dxPSdxLCLnkRS24;rbIDE1724;FMXTee;dxPScxExtCommonRS24;dxPScxPivotGridLnkRS24;soaprtl;DbxCommonDriver;JvManagedThreads;ACBr_NFSeDanfseRL;xmlrtl;soapmidas;JvTimeFramework;fmxobj;dxSkinMcSkinRS24;rtl;dxLayoutControlRS24;DbxClientDriver;cxGridRS24;dxSkinBlueRS24;frxFD24;dxSpellCheckerRS24;JvSystem;cxLibraryRS24;dxSkinStardustRS24;JvStdCtrls;dxSkinCaramelRS24;rbRAP1724;appanalytics;dxSkinsCoreRS24;dxDBXServerModeRS24;fsTee24;dxMapControlRS24;IndyIPClient;dxSkinHighContrastRS24;ACBr_Convenio115;bindcompvcl;vcldbx;dxSkinTheAsphaltWorldRS24;rbBDE1724;TeeUI;frxe24;cxPageControlRS24;cxEditorsRS24;dxPsPrVwAdvRS24;dxSkinSevenClassicRS24;VclSmp;cxSchedulerRibbonStyleEventEditorRS24;JvDocking;JvPascalInterpreter;dxSkinPumpkinRS24;JclVcl;dxSkinscxPCPainterRS24;dxPSPrVwRibbonRS24;ACBr_Boleto;ACBr_SEF2;dxSkinSevenRS24;FMXfsDB24;JvControls;JvPrintPreview;ACBr_NFSe;dxdborRS24;dxmdsRS24;cxSchedulerGridRS24;RESTComponents;dxHttpIndyRequestRS24;rbDAD1724;cxPivotGridRS24;DBXInterBaseDriver;rbTCUI1724;ACBr_TEFD;dclRBADO1724;JvGlobus;svnui;dxdbtrRS24;dxSkinMetropolisRS24;dxSkinMoneyTwinsRS24;dxPScxPCProdRS24;JvMM;dclRBE1724;ACBr_MDFeDamdfeRL;unidacfmx240;dxWizardControlRS24;bindcompfmx;dxPSdxOCLnkRS24;dxBarExtDBItemsRS24;JvNet;unidacvcl240;dxPSdxFCLnkRS24;inetdb;cxSchedulerTreeBrowserRS24;dxSkinOffice2016ColorfulRS24;JvAppFrm;rbDBE1724;frxADO24;ACBr_Diversos;ACBr_TXTComum;FmxTeeUI;unidac240;FireDACIBDriver;fmx;fmxdae;dxSkinSpringTimeRS24;dxSkinValentineRS24;dxSkinLondonLiquidSkyRS24;dxSkinWhiteprintRS24;JvWizards;ACBr_Ponto;dbexpress;IndyCore;dxSkiniMaginaryRS24;rbDIDE1724;rbRest1724;dxTileControlRS24;dxSkinOffice2016DarkRS24;dsnap;FMXfs24;dxSkinOffice2007PinkRS24;fsADO24;FireDACCommon;cxDataRS24;bdertl;dxPSdxSpreadSheetLnkRS24;JvDB;JvPageComps;rbUSERDesign1724;ACBr_PCNComum;ACBR_DeSTDA;dxSkinDevExpressStyleRS24;soapserver;ACBr_SAT;dac240;JclDeveloperTools;dxBarRS24;dxSkinMetropolisDarkRS24;JvCmp;DBXMySQLDriver;dxPSRichEditControlLnkRS24;dxPScxCommonRS24;ACBr_Sintegra;FireDACCommonODBC;FireDACCommonDriver;ACBr_GNRE;NasajonSkinRS24;inet;rbUSER1724;IndyIPCommon;dxSkinVS2010RS24;JvCustom;vcl;ACBr_NFeDanfeRL;dxSkinSharpPlusRS24;JvXPCtrls;dxPSdxDBOCLnkRS24;ACBr_SATExtratoESCPOS;FMXfsIBX24;dxThemeRS24;dxSkinOffice2007GreenRS24;TeeDB;FireDAC;FMXfsADO24;dxPScxGridLnkRS24;dxPScxVGridLnkRS24;JvCore;ACBr_Comum;JvCrypt;FireDACSqliteDriver;FireDACPgDriver;ibmonitor;dxSkinOffice2010BlueRS24;ACBr_GNREGuiaRL;dxServerModeRS24;JvDlgs;JvRuntimeDesign;ibxpress;Tee;dacvcl240;ibxbindings;cxSchedulerRS24;vclwinx;dxSkinsdxDLPainterRS24;frxIBX24;dxPSCoreRS24;dxSkinOffice2007BlueRS24;ACBr_OpenSSL;frxTee24;CustomIPTransport;vcldsnap;dxSkinGlassOceansRS24;dxRibbonCustomizationFormRS24;dxPScxSchedulerLnkRS24;ACBr_DFeComum;dxSkinSummer2008RS24;dxSkinDarkRoomRS24;bindcomp;dxSkinFoggyRS24;ACBr_Serial;dxorgcRS24;dxSkinOffice2010SilverRS24;frce;frxBDE24;ACBr_BlocoX;nsXEUtil;dxRichEditControlRS24;dxSkinsdxNavBarPainterRS24;dbxcds;fsBDE24;adortl;dxSkinSilverRS24;ACBr_NFCeECFVirtual;dxSkinVisualStudio2013DarkRS24;dxComnRS24;rbTC1724;cxVerticalGridRS24;dxFlowChartRS24;frxDB24;rbRTL1724;dsnapxml;dbrtl;inetdbxpress;IndyProtocols;dxGaugeControlRS24;dxSkinOffice2007SilverRS24;dxSkinLiquidSkyRS24;JclContainers;dclRBDBE1724;ACBr_MTER;fmxase;$(DCC_UsePackage)</DCC_UsePackage>\r\n" + 
			"        <Manifest_File>$(BDS)\\bin\\default_app.manifest</Manifest_File>\r\n" + 
			"        <DCC_Namespace>Winapi;System.Win;Data.Win;Datasnap.Win;Web.Win;Soap.Win;Xml.Win;Bde;$(DCC_Namespace)</DCC_Namespace>\r\n" + 
			"        <VerInfo_IncludeVerInfo>true</VerInfo_IncludeVerInfo>\r\n" + 
			"        <VerInfo_Locale>1033</VerInfo_Locale>\r\n" + 
			"        <BT_BuildType>Debug</BT_BuildType>\r\n" + 
			"        <VerInfo_Keys>CompanyName=;FileDescription=$(MSBuildProjectName);FileVersion=1.0.0.0;InternalName=;LegalCopyright=;LegalTrademarks=;OriginalFilename=;ProgramID=com.embarcadero.$(MSBuildProjectName);ProductName=$(MSBuildProjectName);ProductVersion=1.0.0.0;Comments=</VerInfo_Keys>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"'$(Base_Win64)'!=''\">\r\n" + 
			"        <DCC_UsePackage>DBXSqliteDriver;dxSkinBlueprintRS24;dxPSDBTeeChartRS24;dxPSdxGaugeControlLnkRS24;vclactnband;dxSpreadSheetRS24;vclFireDAC;dxDockingRS24;tethering;dxSkinVisualStudio2013BlueRS24;dxPScxTLLnkRS24;dxBarExtItemsRS24;FireDACADSDriver;dxFireDACServerModeRS24;dxPSTeeChartRS24;dxSkinOffice2007BlackRS24;vcltouch;vcldb;dxSkinXmas2008BlueRS24;dxSkinscxSchedulerPainterRS24;dxSkinsdxBarPainterRS24;dxSkinOffice2010BlackRS24;dxADOServerModeRS24;dxGDIPlusRS24;dxPSdxDBTVLnkRS24;vclib;dxSkinLilianRS24;dxNavBarRS24;vclx;cxTreeListRS24;dxSkinDevExpressDarkStyleRS24;dxtrmdRS24;RESTBackendComponents;dxRibbonRS24;VCLRESTComponents;cxExportRS24;cxPivotGridChartRS24;cxTreeListdxBarPopupMenuRS24;dxSkinOffice2013LightGrayRS24;dxTabbedMDIRS24;vclie;dxSkinVisualStudio2013LightRS24;bindengine;CloudService;FireDACMySQLDriver;cxPivotGridOLAPRS24;dxSkinSharpRS24;dxSkinBlackRS24;dxPSLnksRS24;bindcompdbx;dxSkinCoffeeRS24;IndyIPServer;dxCoreRS24;IndySystem;dxSkinsdxRibbonPainterRS24;dxSkinOffice2013DarkGrayRS24;dsnapcon;FireDACMSAccDriver;fmxFireDAC;vclimg;dxBarDBNavRS24;dxSkinDarkSideRS24;dxSkinOffice2013WhiteRS24;dxPSdxLCLnkRS24;FMXTee;dxPScxExtCommonRS24;dxPScxPivotGridLnkRS24;soaprtl;DbxCommonDriver;xmlrtl;soapmidas;fmxobj;dxSkinMcSkinRS24;rtl;dxLayoutControlRS24;DbxClientDriver;cxGridRS24;dxSkinBlueRS24;dxSpellCheckerRS24;cxLibraryRS24;dxSkinStardustRS24;dxSkinCaramelRS24;appanalytics;dxSkinsCoreRS24;dxDBXServerModeRS24;dxMapControlRS24;IndyIPClient;dxSkinHighContrastRS24;bindcompvcl;dxSkinTheAsphaltWorldRS24;TeeUI;cxPageControlRS24;cxEditorsRS24;dxPsPrVwAdvRS24;dxSkinSevenClassicRS24;VclSmp;cxSchedulerRibbonStyleEventEditorRS24;dxSkinPumpkinRS24;dxSkinscxPCPainterRS24;dxPSPrVwRibbonRS24;dxSkinSevenRS24;dxdborRS24;dxmdsRS24;cxSchedulerGridRS24;RESTComponents;dxHttpIndyRequestRS24;cxPivotGridRS24;DBXInterBaseDriver;dxdbtrRS24;dxSkinMetropolisRS24;dxSkinMoneyTwinsRS24;dxPScxPCProdRS24;dxWizardControlRS24;bindcompfmx;dxPSdxOCLnkRS24;dxBarExtDBItemsRS24;dxPSdxFCLnkRS24;inetdb;cxSchedulerTreeBrowserRS24;dxSkinOffice2016ColorfulRS24;FmxTeeUI;FireDACIBDriver;fmx;fmxdae;dxSkinSpringTimeRS24;dxSkinValentineRS24;dxSkinLondonLiquidSkyRS24;dxSkinWhiteprintRS24;dbexpress;IndyCore;dxSkiniMaginaryRS24;dxTileControlRS24;dxSkinOffice2016DarkRS24;dsnap;dxSkinOffice2007PinkRS24;FireDACCommon;cxDataRS24;dxPSdxSpreadSheetLnkRS24;dxSkinDevExpressStyleRS24;soapserver;dxBarRS24;dxSkinMetropolisDarkRS24;DBXMySQLDriver;dxPSRichEditControlLnkRS24;dxPScxCommonRS24;FireDACCommonODBC;FireDACCommonDriver;inet;IndyIPCommon;dxSkinVS2010RS24;vcl;dxSkinSharpPlusRS24;dxPSdxDBOCLnkRS24;dxThemeRS24;dxSkinOffice2007GreenRS24;TeeDB;FireDAC;dxPScxGridLnkRS24;dxPScxVGridLnkRS24;FireDACSqliteDriver;FireDACPgDriver;ibmonitor;dxSkinOffice2010BlueRS24;dxServerModeRS24;ibxpress;Tee;ibxbindings;cxSchedulerRS24;vclwinx;dxSkinsdxDLPainterRS24;dxPSCoreRS24;dxSkinOffice2007BlueRS24;CustomIPTransport;vcldsnap;dxSkinGlassOceansRS24;dxRibbonCustomizationFormRS24;dxPScxSchedulerLnkRS24;dxSkinSummer2008RS24;dxSkinDarkRoomRS24;bindcomp;dxSkinFoggyRS24;dxorgcRS24;dxSkinOffice2010SilverRS24;nsXEUtil;dxRichEditControlRS24;dxSkinsdxNavBarPainterRS24;dbxcds;adortl;dxSkinSilverRS24;dxSkinVisualStudio2013DarkRS24;dxComnRS24;cxVerticalGridRS24;dxFlowChartRS24;dsnapxml;dbrtl;inetdbxpress;IndyProtocols;dxGaugeControlRS24;dxSkinOffice2007SilverRS24;dxSkinLiquidSkyRS24;fmxase;$(DCC_UsePackage)</DCC_UsePackage>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"'$(Cfg_1)'!=''\">\r\n" + 
			"        <DCC_Define>DEBUG;$(DCC_Define)</DCC_Define>\r\n" + 
			"        <DCC_DebugDCUs>true</DCC_DebugDCUs>\r\n" + 
			"        <DCC_Optimize>false</DCC_Optimize>\r\n" + 
			"        <DCC_GenerateStackFrames>true</DCC_GenerateStackFrames>\r\n" + 
			"        <DCC_DebugInfoInExe>true</DCC_DebugInfoInExe>\r\n" + 
			"        <DCC_RemoteDebug>true</DCC_RemoteDebug>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"'$(Cfg_1_Win32)'!=''\">\r\n" + 
			"        <VerInfo_Locale>1033</VerInfo_Locale>\r\n" + 
			"        <VerInfo_IncludeVerInfo>true</VerInfo_IncludeVerInfo>\r\n" + 
			"        <AppEnableHighDPI>true</AppEnableHighDPI>\r\n" + 
			"        <AppEnableRuntimeThemes>true</AppEnableRuntimeThemes>\r\n" + 
			"        <DCC_RemoteDebug>false</DCC_RemoteDebug>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"'$(Cfg_2)'!=''\">\r\n" + 
			"        <DCC_LocalDebugSymbols>false</DCC_LocalDebugSymbols>\r\n" + 
			"        <DCC_Define>RELEASE;$(DCC_Define)</DCC_Define>\r\n" + 
			"        <DCC_SymbolReferenceInfo>0</DCC_SymbolReferenceInfo>\r\n" + 
			"        <DCC_DebugInformation>0</DCC_DebugInformation>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <PropertyGroup Condition=\"'$(Cfg_2_Win32)'!=''\">\r\n" + 
			"        <AppEnableRuntimeThemes>true</AppEnableRuntimeThemes>\r\n" + 
			"        <AppEnableHighDPI>true</AppEnableHighDPI>\r\n" + 
			"    </PropertyGroup>\r\n" + 
			"    <ItemGroup>\r\n" + 
			"        <DelphiCompile Include=\"$(MainSource)\">\r\n" + 
			"            <MainSource>MainSource</MainSource>\r\n" + 
			"        </DelphiCompile>\r\n" + 
			"        <UNITS>\r\n" + 
			"        <BuildConfiguration Include=\"Release\">\r\n" + 
			"            <Key>Cfg_2</Key>\r\n" + 
			"            <CfgParent>Base</CfgParent>\r\n" + 
			"        </BuildConfiguration>\r\n" + 
			"        <BuildConfiguration Include=\"Base\">\r\n" + 
			"            <Key>Base</Key>\r\n" + 
			"        </BuildConfiguration>\r\n" + 
			"        <BuildConfiguration Include=\"Debug\">\r\n" + 
			"            <Key>Cfg_1</Key>\r\n" + 
			"            <CfgParent>Base</CfgParent>\r\n" + 
			"        </BuildConfiguration>\r\n" + 
			"    </ItemGroup>\r\n" + 
			"    <ProjectExtensions>\r\n" + 
			"        <Borland.Personality>Delphi.Personality.12</Borland.Personality>\r\n" + 
			"        <Borland.ProjectType>Application</Borland.ProjectType>\r\n" + 
			"        <BorlandProject>\r\n" + 
			"            <Delphi.Personality>\r\n" + 
			"                <Source>\r\n" + 
			"                    <Source Name=\"MainSource\">default.dpr</Source>\r\n" + 
			"                </Source>\r\n" + 
			"                <Excluded_Packages>\r\n" + 
			"                    <Excluded_Packages Name=\"$(BDSBIN)\\dcloffice2k240.bpl\">Microsoft Office 2000 Sample Automation Server Wrapper Components</Excluded_Packages>\r\n" + 
			"                    <Excluded_Packages Name=\"$(BDSBIN)\\dclofficexp240.bpl\">Microsoft Office XP Sample Automation Server Wrapper Components</Excluded_Packages>\r\n" + 
			"                </Excluded_Packages>\r\n" + 
			"            </Delphi.Personality>\r\n" + 
			"            <Deployment Version=\"3\">\r\n" + 
			"                <DeployFile LocalName=\"..\\..\\..\\..\\..\\..\\bin\\default.exe\" Configuration=\"Debug\" Class=\"ProjectOutput\">\r\n" + 
			"                    <Platform Name=\"Win32\">\r\n" + 
			"                        <RemoteName>default.exe</RemoteName>\r\n" + 
			"                        <Overwrite>true</Overwrite>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployFile>\r\n" + 
			"                <DeployClass Name=\"DependencyModule\">\r\n" + 
			"                    <Platform Name=\"Win32\">\r\n" + 
			"                        <Operation>0</Operation>\r\n" + 
			"                        <Extensions>.dll;.bpl</Extensions>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                        <Extensions>.dylib</Extensions>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"OSX32\">\r\n" + 
			"                        <RemoteDir>Contents\\MacOS</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                        <Extensions>.dylib</Extensions>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                        <Extensions>.dylib</Extensions>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                        <Extensions>.dylib</Extensions>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"ProjectOSXResource\">\r\n" + 
			"                    <Platform Name=\"OSX32\">\r\n" + 
			"                        <RemoteDir>Contents\\Resources</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"AndroidClassesDexFile\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>classes</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"AdditionalDebugSymbols\">\r\n" + 
			"                    <Platform Name=\"Win32\">\r\n" + 
			"                        <RemoteDir>Contents\\MacOS</RemoteDir>\r\n" + 
			"                        <Operation>0</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"OSX32\">\r\n" + 
			"                        <RemoteDir>Contents\\MacOS</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"iPad_Launch768\">\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"Android_LauncherIcon144\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>res\\drawable-xxhdpi</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"AndroidLibnativeMipsFile\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>library\\lib\\mips</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Required=\"true\" Name=\"ProjectOutput\">\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"Win32\">\r\n" + 
			"                        <Operation>0</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"Linux64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"OSX32\">\r\n" + 
			"                        <RemoteDir>Contents\\MacOS</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>library\\lib\\armeabi-v7a</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"DependencyFramework\">\r\n" + 
			"                    <Platform Name=\"Win32\">\r\n" + 
			"                        <Operation>0</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"OSX32\">\r\n" + 
			"                        <RemoteDir>Contents\\MacOS</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                        <Extensions>.framework</Extensions>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"ProjectUWPManifest\">\r\n" + 
			"                    <Platform Name=\"Win32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"Win64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"iPhone_Launch640\">\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"iPad_Launch1024\">\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"ProjectiOSDeviceDebug\">\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <RemoteDir>..\\$(PROJECTNAME).app.dSYM\\Contents\\Resources\\DWARF</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <RemoteDir>..\\$(PROJECTNAME).app.dSYM\\Contents\\Resources\\DWARF</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"iPhone_Launch320\">\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"ProjectiOSInfoPList\">\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"AndroidLibnativeArmeabiFile\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>library\\lib\\armeabi</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"DebugSymbols\">\r\n" + 
			"                    <Platform Name=\"Win32\">\r\n" + 
			"                        <Operation>0</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"OSX32\">\r\n" + 
			"                        <RemoteDir>Contents\\MacOS</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"iPad_Launch1536\">\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"Android_SplashImage470\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>res\\drawable-normal</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"Android_LauncherIcon96\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>res\\drawable-xhdpi</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"Android_SplashImage640\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>res\\drawable-large</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"iPhone_Launch640x1136\">\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"UWP_DelphiLogo44\">\r\n" + 
			"                    <Platform Name=\"Win32\">\r\n" + 
			"                        <RemoteDir>Assets</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"Win64\">\r\n" + 
			"                        <RemoteDir>Assets</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"ProjectiOSEntitlements\">\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <RemoteDir>..\\</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <RemoteDir>..\\</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"Android_LauncherIcon72\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>res\\drawable-hdpi</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"AndroidGDBServer\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>library\\lib\\armeabi-v7a</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"ProjectOSXInfoPList\">\r\n" + 
			"                    <Platform Name=\"OSX32\">\r\n" + 
			"                        <RemoteDir>Contents</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"ProjectOSXEntitlements\">\r\n" + 
			"                    <Platform Name=\"OSX32\">\r\n" + 
			"                        <RemoteDir>..\\</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"UWP_DelphiLogo150\">\r\n" + 
			"                    <Platform Name=\"Win32\">\r\n" + 
			"                        <RemoteDir>Assets</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"Win64\">\r\n" + 
			"                        <RemoteDir>Assets</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"iPad_Launch2048\">\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"AndroidSplashStyles\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>res\\values</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"Android_SplashImage426\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>res\\drawable-small</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"AndroidSplashImageDef\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>res\\drawable</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"ProjectiOSResource\">\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"ProjectAndroidManifest\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"Android_DefaultAppIcon\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>res\\drawable</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"File\">\r\n" + 
			"                    <Platform Name=\"Win32\">\r\n" + 
			"                        <Operation>0</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>0</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"OSX32\">\r\n" + 
			"                        <RemoteDir>Contents\\Resources\\StartUp\\</RemoteDir>\r\n" + 
			"                        <Operation>0</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>0</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <Operation>0</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>0</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"AndroidServiceOutput\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>library\\lib\\armeabi-v7a</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Required=\"true\" Name=\"DependencyPackage\">\r\n" + 
			"                    <Platform Name=\"Win32\">\r\n" + 
			"                        <Operation>0</Operation>\r\n" + 
			"                        <Extensions>.bpl</Extensions>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                        <Extensions>.dylib</Extensions>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"OSX32\">\r\n" + 
			"                        <RemoteDir>Contents\\MacOS</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                        <Extensions>.dylib</Extensions>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                        <Extensions>.dylib</Extensions>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSSimulator\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                        <Extensions>.dylib</Extensions>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"Android_LauncherIcon48\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>res\\drawable-mdpi</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"Android_SplashImage960\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>res\\drawable-xlarge</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"Android_LauncherIcon36\">\r\n" + 
			"                    <Platform Name=\"Android\">\r\n" + 
			"                        <RemoteDir>res\\drawable-ldpi</RemoteDir>\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <DeployClass Name=\"ProjectiOSDeviceResourceRules\">\r\n" + 
			"                    <Platform Name=\"iOSDevice64\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                    <Platform Name=\"iOSDevice32\">\r\n" + 
			"                        <Operation>1</Operation>\r\n" + 
			"                    </Platform>\r\n" + 
			"                </DeployClass>\r\n" + 
			"                <ProjectRoot Platform=\"iOSDevice64\" Name=\"$(PROJECTNAME).app\"/>\r\n" + 
			"                <ProjectRoot Platform=\"Win64\" Name=\"$(PROJECTNAME)\"/>\r\n" + 
			"                <ProjectRoot Platform=\"iOSDevice32\" Name=\"$(PROJECTNAME).app\"/>\r\n" + 
			"                <ProjectRoot Platform=\"Linux64\" Name=\"$(PROJECTNAME)\"/>\r\n" + 
			"                <ProjectRoot Platform=\"Win32\" Name=\"$(PROJECTNAME)\"/>\r\n" + 
			"                <ProjectRoot Platform=\"OSX32\" Name=\"$(PROJECTNAME).app\"/>\r\n" + 
			"                <ProjectRoot Platform=\"Android\" Name=\"$(PROJECTNAME)\"/>\r\n" + 
			"                <ProjectRoot Platform=\"iOSSimulator\" Name=\"$(PROJECTNAME).app\"/>\r\n" + 
			"            </Deployment>\r\n" + 
			"            <Platforms>\r\n" + 
			"                <Platform value=\"Win32\">True</Platform>\r\n" + 
			"                <Platform value=\"Win64\">False</Platform>\r\n" + 
			"            </Platforms>\r\n" + 
			"        </BorlandProject>\r\n" + 
			"        <ProjectFileVersion>12</ProjectFileVersion>\r\n" + 
			"    </ProjectExtensions>\r\n" + 
			"    <Import Project=\"$(BDS)\\Bin\\CodeGear.Delphi.Targets\" Condition=\"Exists('$(BDS)\\Bin\\CodeGear.Delphi.Targets')\"/>\r\n" + 
			"    <Import Project=\"$(APPDATA)\\Embarcadero\\$(BDSAPPDATABASEDIR)\\$(PRODUCTVERSION)\\UserTools.proj\" Condition=\"Exists('$(APPDATA)\\Embarcadero\\$(BDSAPPDATABASEDIR)\\$(PRODUCTVERSION)\\UserTools.proj')\"/>\r\n" + 
			"    <Import Project=\"$(MSBuildProjectName).deployproj\" Condition=\"Exists('$(MSBuildProjectName).deployproj')\"/>\r\n" + 
			"</Project>\r\n" + 
			"";
	
	
	public static void main(String[] args) {

		long inicio = System.currentTimeMillis();
		
		// Verificando se não foram passados parâmetros:
		if (args.length < 1) {
			System.out.println("");
			System.out.println("");
			System.out.println("Por favor, indique o objetivo do build (primeiro parâmetro). Exemplo de uso:");
			imprimirFormaUso();
			
			System.exit(1);
			return;
		}
		
		Boolean isClean = false;
		Boolean isValidate = false;
		String parProjeto = "";
		BuildMode bm = BuildMode.debug;
		BuildTarget buildTarget = BuildTarget.build;
		
		// Analisando cada parâmetro:
		for (int i = 0; i < args.length; i++) {
			String s = args[i];
			
			if (PAR_HELP.contains(s)) { // Verificando se é uma chamada ao help:
				imprimirFormaUso();
				return;
			} else if (s.equals(PAR_BUILD_CLEAN)) { // Verificando se é uma chamada ao clean:
				isClean = true;
			} else if (s.equals(PAR_BUILD_VALIDATE)) {
				isValidate = true;
			} else if (s.equals(BuildMode.debug.toString())) {
				bm = BuildMode.debug; // Resolvendo o build mode
			} else if (s.equals(BuildMode.release.toString())) {
				bm = BuildMode.release; // Resolvendo o build mode
			} else if (s.equals(BuildTarget.build.toString())) {
				buildTarget = BuildTarget.build; // Resolvendo o build target
			} else if (s.equals(BuildTarget.compile.toString())) {
				buildTarget = BuildTarget.compile; // Resolvendo o build target
			} else {
				parProjeto = s;
			}
		}
		
//		// Pegando o prâmetro do projeto (objetivo do build):
//		String parProjeto = args[0];
		
//		// Verificando se é uma chamada ao help:
//		if (PAR_HELP.contains(parProjeto)) {
//			imprimirFormaUso();
//			return;
//		}
		
//		// Verificando se não é uma chamada ao clean (para limpar a cache):
//		if (parProjeto.equals(PAR_BUILD_CLEAN_CACHE)) {
//			if (!limparCache()) {
//				System.exit(1);
//			}
//			
//			return;
//		}
		
		// Carregando parâmetros de configuração do build:
		ParametrosNsjbuild parametros = carregaParametrosBuild();
		
		// Verificando se não é uma chamada ao clean (para limpar a cache):
		if (isClean) {
			if (!(new MainClean()).execute(parametros)) {
				System.exit(1);
			}
			return;
		}
		
		// Verificando se não é uma chamada ao validate (para validar as dependências e/ou replicação de nomes de units):
		if (isValidate) {
			if (!(new MainValidate()).execute(args, parametros)) {
				System.exit(1);
			}
			return;
		}
		
//		// Resolvendo o build mode:
//		BuildMode bm = resolveBuildMode(args);
		
		// Carregando a lista de projetos:
		List<ProjetoWrapper> listaProjetos = XMLHandler.carregaListaDeProjetos(parametros);
		if (listaProjetos == null) {
			System.out.println("");
			System.out.println("");
			System.out.println("Não foi possível carregar os XMLs de descrição dos projetos.");

			System.exit(1);
			return;
		}
		
		// Fazendo parser de units por projeto:
		Iterator<ProjetoWrapper> it = listaProjetos.iterator();
		while(it.hasNext()) {
			ProjetoWrapper projeto = it.next();

			File arquivoDproj = new File(parametros.getErpPath() + projeto.getProjeto().getPath());

			Set<Unit> units;
			try {
				units = InterpretadorDproj.extrairIncludes(arquivoDproj);
			} catch (IOException e) {
				System.out.println("Erro ao interpretar lista de units por projeto.");
				e.printStackTrace();
				
				System.exit(1);
				return;
			}
			
			for(Unit u: units) {
				String p;
				try {
					p = new File(new File(parametros.getErpPath() + projeto.getProjeto().getPath()).getParentFile().getAbsolutePath() + File.separator + u.getPath()).getCanonicalPath();
				} catch (IOException e) {
					System.out.println("Erro resolver caminho da unit.");
					e.printStackTrace();
					
					System.exit(1);
					return;
				}

				u.setPath(p);
			}
			
			projeto.setUnits(units);
		}		
		
		// Verificando se o projeto passado existe:
		boolean isBuildAlterados = false;
		
		if (!parProjeto.equals(PAR_BUILD_UPDATE)) {
			boolean achou = false;
			for (ProjetoWrapper p: listaProjetos) {
				if (p.getProjeto().getNome().equals(parProjeto)) {
					achou = true;
					break;
				}
			}
			
			if (!achou) {
				System.out.println("");
				System.out.println("");
				System.out.println("Projeto não encontrado: " + parProjeto);

				System.exit(1);
				return;
			}
		}
		
		// Verificando se foi passado o parâmetro de build force:
		boolean isBuildForce = false;
//		BuildTarget buildTarget = BuildTarget.build;
		
		// Setando o inline como ON se o target for "Build", pois neste caso independente do INLINE, as
		// dependencias sempre exigem de recompilação.
		if (buildTarget == BuildTarget.build) {
			parametros.setInline(true);
		}
		
		try {
			long antesGrafo = System.currentTimeMillis();
			System.out.println("Montando grafo...");
			Grafo grafo = Grafo.montaGrafo(parametros, listaProjetos, isBuildForce, isBuildAlterados);
			Grafo grafoClone = grafo.getCloneGrafo();
			Double intervaloGrafo = ((System.currentTimeMillis() - antesGrafo)/1000.0)/60.0;
			System.out.println("Grafo completo. Tempo: " + String.format("%.4f", intervaloGrafo) + " minutos.");
			
			Compilador compilador = new Compilador(parametros.getMaxProcessos().intValue(), bm, parametros.getBatchName(), buildTarget);
			
			if (!callPreBuildBatch(parametros)) {
				System.exit(1);
				return;
			}
			
			Queue<No> simulacaoCompilacao;
			No nofila = null;
			if (!parProjeto.equals(PAR_BUILD_UPDATE) && !isBuildAlterados) {
				simulacaoCompilacao = compilador.simularCompilacaoProjetoComDependencias(grafoClone, parProjeto);
				compilador.setQtdProjetosCompilar(simulacaoCompilacao.size());
//				
//				compilador.compilaProjetoComDependencias(grafo, parProjeto);
				
				String unitsDpr = "";
				String unitsDproj = "";
				
				Set<String> pathsUnits = new HashSet<>();

				Iterator<No> it2 = simulacaoCompilacao.iterator();
				while(it2.hasNext()) {
					nofila = it2.next();
					if(!it2.hasNext()) {
						break;
					}
					
					for(Unit u: nofila.getProjeto().getUnits()) {
						String pathunit = new File(u.getPath()).getCanonicalPath();
//						System.out.println(pathunit);
						
						if (u.getNome().toLowerCase().contains("ufrmdebug")) {
							continue;
						}

						if (u.getNome().toLowerCase().contains("formdebug")) {
							continue;
						}
						
						if (u.getNome().toLowerCase().contains("ufrmprincipal")) {
							continue;
						}
						
						if (pathunit.endsWith(".dcp")) {
							continue;
						}
						
						if (pathsUnits.contains(pathunit)) {
							continue;
						}
						pathsUnits.add(pathunit);
						
						String unitStr = PADRAO_UNIT_DRP.replace("<NOME_UNIT>", u.getNome()).replace("<CAMINHO_UNIT>", u.getPath());

						unitsDpr = unitsDpr + "\r\n," + unitStr;

						unitStr = PADRAO_UNIT_DPROJ.replace("<NOME_UNIT>", u.getNome()).replace("<CAMINHO_UNIT>", u.getPath());

						unitsDproj = unitsDproj + "\r\n" + unitStr;
					}
				}
				
//				System.out.println(unitsDpr);
//				System.out.println(unitsDproj);
				
				String pathDproj = "C:\\\\@work\\\\erp\\\\source\\\\desktop_new\\\\commonfeature\\\\default\\\\package\\\\default.dproj";
				String pathDpr = "C:\\\\@work\\\\erp\\\\source\\\\desktop_new\\\\commonfeature\\\\default\\\\package\\\\default.dpr";
				
				File fDproj = new File(pathDproj);
				File fDpr = new File(pathDpr);
				
				try (
						FileOutputStream fos = new FileOutputStream(fDpr);
						OutputStreamWriter osw = new OutputStreamWriter(fos);

						FileOutputStream fos2 = new FileOutputStream(fDproj);
						OutputStreamWriter osw2 = new OutputStreamWriter(fos2);
				) {
					osw.write(DEFAULT_DPR.replace("<UNITS>", unitsDpr));
					osw2.write(DEFAULT_DPROJ.replace("<UNITS>", unitsDproj));
				}
				
				Projeto projeto = new Projeto();
				projeto.setNome("default");
				projeto.setPath("C:\\\\@work\\\\erp\\\\source\\\\desktop_new\\\\commonfeature\\\\default\\\\package\\\\default.dproj");
				
				ProjetoWrapper proj = new ProjetoWrapper();
				proj.setProjeto(projeto);
				
				No n = new No("default", "C:\\@work\\erp\\source\\desktop_new\\commonfeature\\default\\package\\default.dproj", proj);
				
				if(nofila != null) {
					nofila.getSaidas().clear();
					nofila.addSaida(n);
				}
				
				compilador.compilar(n);
				if(nofila != null) {
					System.out.println("Oi");
					compilador.compilar(nofila);
				}
				
				return;
				
			} else {
				simulacaoCompilacao = compilador.simulateCompileAll(grafoClone);
				compilador.setQtdProjetosCompilar(simulacaoCompilacao.size());
				
				compilador.compileAll(grafo);
			}
			
			while (!compilador.isAborted() && compilador.existsThreadAtiva()) {
				Thread.sleep(2000);
			}
			
			// Imprimindo mensagem de finalização:
			long fim = System.currentTimeMillis();
			Double intervaloMinutos = ((fim - inicio)/1000.0)/60.0;
			
			if (!compilador.isAborted()) {
				System.out.println("BUILD FINALIZADO COM SUCESSO. Demorou " + String.format("%.4f", intervaloMinutos) + " (minutos). Quantidade de projetos compilados: " + compilador.getTotalCompilados());
				return;
			} else {
				System.out.println("Build com falhas. Demorou " + String.format("%.4f", intervaloMinutos) + " (minutos). Quantidade de projetos compilados: " + compilador.getTotalCompilados());
				System.exit(1);
				return;
			}
		} catch (GrafoCiclicoException e) {
			System.out.println("");
			System.out.println("");
			System.out.println(e.getMessage());
			System.exit(1);
			return;
		} catch (InterruptedException e) {
			System.out.println("");
			System.out.println("");
			System.out.println("Erro de interrupção de thread durante a compilação:");
			e.printStackTrace();
			System.exit(1);
			return;
		} catch (IOException e) {
			System.out.println("");
			System.out.println("");
			System.out.println("Erro de IO ao checar status de compilação dos projetos:");
			e.printStackTrace();
			System.exit(1);
			return;
		} catch (DependenciaInvalidaException e) {
			System.out.println("");
			System.out.println("");
			System.out.println(e.getMessage());
			System.exit(1);
			return;
		} catch (ProjectFileNotFoundException e) {
			System.out.println("");
			System.out.println("");
			System.out.println(e.getMessage());
			System.exit(1);
			return;
		}
	}

//	private static BuildMode resolveBuildMode(String[] args) {
//		BuildMode bm = BuildMode.debug;
//		
//		if (args.length < 2) {
//			return bm;
//		}
//		
//		if (args[1].equals(BuildMode.debug.toString())) {
//			bm = BuildMode.debug;
//		}
//		if (args[1].equals(BuildMode.release.toString())) {
//			bm = BuildMode.release;
//		}
//		return bm;
//	}

	private static ParametrosNsjbuild carregaParametrosBuild() {
		ParametrosNsjbuild parametros = new ParametrosNsjbuild();
		parametros.setErpPath("c:\\@work\\erp");
		parametros.setMaxProcessos(new BigInteger("2"));
		parametros.setXmlsProjectsPath(new File("xmls").getAbsolutePath());
		parametros.setBatchName("internal_build.bat");
		parametros.setBatchPrebuild("prebuild.bat");
		parametros.setBatchClean("clean.bat");
		parametros.setInline(true);
		
		File fileParametros = new File("nsjBuildParameters.xml");
		if (fileParametros.exists()) {
			XMLHandler xmlHandler = new XMLHandler();
			
			try {
				parametros = xmlHandler.carregaXMLParametros(fileParametros);
			} catch (JAXBException e) {
				System.out.println("");
				System.out.println("");
				System.out.println("Erro ao ler XML de configuração do nsjBuild:");
				e.printStackTrace();
			}
		}
		
		// Adicionando o separador de arquivo no final do path do ERP (se necessário):
		if (!parametros.getErpPath().endsWith("\\") && !parametros.getErpPath().endsWith("/")) {
			parametros.setErpPath(parametros.getErpPath() + File.separator);
		}
		
		return parametros;
	}
	
	private static boolean callPreBuildBatch(ParametrosNsjbuild parametros) {

		System.out.println("Chamando o batch de 'pre-build'...");
		
		try {
			Process p = Runtime.getRuntime().exec(parametros.getBatchPrebuild());
			
			if(p.waitFor() != 0) {
				System.out.println("");
				System.out.println("");
				System.out.println("");
				System.out.println("");
				
				System.out.println("Erro ao executar batch de 'pre-build':");
				
				InputStream error = p.getInputStream();
				for (int i = 0; i < error.available(); i++) {
					System.out.print("" + (char)error.read());
				}
				System.out.println("");
				
				error = p.getErrorStream();
				for (int i = 0; i < error.available(); i++) {
					System.out.print("" + (char)error.read());
				}
				
				return false;
			} else {
				System.out.println("Batch de 'pre-build' executado com sucesso.");
				
				return true;
			}
		} catch (Exception e) {
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			
			System.out.println("Erro ao executar batch de 'pre-build':");
			e.printStackTrace();
			
			return false;
		}
	}
	
	private static void imprimirFormaUso() {
		System.out.println("");
		System.out.println("----------------------------------------------------------------");
		System.out.println("SINTAXE ESPERADA:");
		System.out.println("");
		System.out.println("nsbuild <nome do projeto/update/clean> [debug (default)/release]");
		System.out.println("----------------------------------------------------------------");
		System.out.println("");
		System.out.println("");
		System.out.println("Conceitos importantes:");
		System.out.println("");
		System.out.println("nome do projeto - Especifica o projeto objetivo a ser compilado (todos os projetos - ainda não compilados, ou alterados - na árvore de dependências do mesmo serão compilados à priori).");
		System.out.println("");
		System.out.println("update - Compila todos os projetos disponíveis (ainda não compilados, ou alterados), respeitando a ordem de dependências entre os mesmos.");
		System.out.println("");
		System.out.println("clean - Apaga todas as DCUs e limpa a cache de controle dos projetos compilados (ATENÇÃO: Após ser chamado o clean, uma chamada ao comando 'nsbuild update' será equivalente ao antigo build.bat na opção zero).");
		System.out.println("");
		System.out.println("debug/release - Modo de build, isto é, gera os executáveis em modo debug (para depuração) ou modo de entrega (release).");
		System.out.println("");
		System.out.println("");
		System.out.println("Obs. 1: Utilize o seguinte comando para visualizar este manual de uso: 'nsbuild /?'");
		System.out.println("");
		System.out.println("Obs. 2: Para forçar a recompilação de todos os projetos (antigo build.bat na opção 0), é preciso usar sequencialmente os comandos:");
		System.out.println("nsbuild clean");
		System.out.println("nsbuild update");
	}
}
