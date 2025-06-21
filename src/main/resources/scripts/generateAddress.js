
 import { Lucid, Blockfrost } from "lucid-cardano";
 import * as fs from "fs";

 const SCRIPT_PATH = process.argv[2];
 const BLOCKFROST_KEY = process.argv[3];
 const NETWORK = process.argv[4];

 if (!SCRIPT_PATH || !BLOCKFROST_KEY || !NETWORK) {
   console.error("❌ Missing required arguments: SCRIPT_PATH, BLOCKFROST_KEY, NETWORK");
   process.exit(1);
 }

 async function generateScriptAddress() {
   try {
     let baseUrl;
     switch (NETWORK.toLowerCase()) {
       case "mainnet":
         baseUrl = "https://cardano-mainnet.blockfrost.io/api/v0";
         break;
       case "preprod":
         baseUrl = "https://cardano-preprod.blockfrost.io/api/v0";
         break;
       case "preview":
         baseUrl = "https://cardano-preview.blockfrost.io/api/v0";
         break;
       default:
         console.error(`❌ Unsupported network: ${NETWORK}`);
         process.exit(1);
     }

     const provider = new Blockfrost(baseUrl, BLOCKFROST_KEY);

     // ✅ Proper initialization
     const lucid = await Lucid.new(provider, capitalize(NETWORK));

     const scriptContent = fs.readFileSync(SCRIPT_PATH, "utf8");
     let validator;

     try {
       const parsed = JSON.parse(scriptContent);
       validator = parsed.cborHex
         ? { type: "PlutusV2", script: parsed.cborHex }
         : parsed;
     } catch {
       validator = { type: "PlutusV2", script: scriptContent.trim() };
     }

     const scriptAddress = lucid.utils.validatorToAddress(validator);
     console.log(scriptAddress);
   } catch (err) {
     console.error("❌ Error generating script address:", err.message);
     process.exit(1);
   }
 }

 function capitalize(word) {
   return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
 }

 generateScriptAddress();
