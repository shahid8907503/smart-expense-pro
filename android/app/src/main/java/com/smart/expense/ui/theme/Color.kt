package com.smart.expense.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// STARLING BANK × WEALTHSIMPLE BENTO NEANK DESIGN SYSTEM
// (Inspired by Starling Bank Oceanic Pine, Wealthsimple Bento, & Monzo Receipts)
// =========================================================================

// Starling Signature Brand Colors (Deep Oceanic Pine & Sea Glass)
val StarlingPinePrimary = Color(0xFF042B31)       // Deep Oceanic Pine 900 (Starling Signature)
val StarlingPineDark = Color(0xFF021B1F)          // Abyssal Pine 950 (Hero Card base)
val StarlingPineMedium = Color(0xFF0B4E5A)        // Rich Sea Pine 800
val StarlingPineLight = Color(0xFF146A7A)         // Vibrant Oceanic Teal 600
val StarlingPineSubtle = Color(0xFFE6F4F6)        // Ultra-soft Sea Pine pill container (50)
val StarlingPineNavy = Color(0xFF06181C)          // Midnight Abyssal 1000

// Starling & Wealthsimple Electric Accents
val StarlingMintAccent = Color(0xFF00E676)        // Electric Jade / Mint Glow (Positive Cashflow & Badges)
val StarlingMintContainer = Color(0xFFD1FAE5)     // Soft Jade Pill
val StarlingMintDark = Color(0xFF059669)          // Forest Jade 600

// Secondary Accents (Solar Amber & Gold)
val StarlingSolarAmber = Color(0xFFF59E0B)        // Solar Amber 500 (Warnings & Savings Badges)
val StarlingSolarLight = Color(0xFFFDE68A)        // Soft Buttercup 200
val StarlingSolarContainer = Color(0xFFFEF3C7)    // Translucent Butter Container

// Financial Semantic Indicators
val StarlingGreenIncome = Color(0xFF10B981)       // Vibrant Jade (Positive Cashflow)
val StarlingGreenContainer = Color(0xFFD1FAE5)    // Soft Jade Pill
val StarlingRedExpense = Color(0xFFEF4444)        // Crisp Coral Red (Expenses)
val StarlingRedContainer = Color(0xFFFEE2E2)      // Soft Coral Pill
val StarlingWarningAmber = Color(0xFFF59E0B)      // Warning Amber

// Light Mode Canvas (Wealthsimple Crisp Porcelain & Bento Pure White)
val WealthsimpleLightBg = Color(0xFFF4F7F8)       // Ultra-crisp porcelain canvas (Low Eye Fatigue)
val WealthsimpleLightCard = Color(0xFFFFFFFF)     // Pure white bento card surface
val WealthsimpleLightBorder = Color(0xFFE2E8F0)   // Subtle 1px hairline border (Slate 200)
val WealthsimpleLightTextPrimary = Color(0xFF0A1B24) // Deep Obsidian Slate text (Slate 900)
val WealthsimpleLightTextSecondary = Color(0xFF5A7184) // Soft Slate 500
val WealthsimpleLightTextMuted = Color(0xFF94A3B8)// Slate 400 muted

// Dark Mode Canvas (Starling Deep Abyssal Slate - Low Glare)
val StarlingDarkBg = Color(0xFF061014)            // Deep Abyssal Slate Canvas
val StarlingDarkCard = Color(0xFF0D1C22)          // Matte Abyssal Card surface
val StarlingDarkCardSurface = Color(0xFF152A33)   // Raised Bento tile
val StarlingDarkBorder = Color(0xFF1E3A46)        // Hairline teal-slate border
val StarlingDarkTextPrimary = Color(0xFFF1F5F9)   // Crisp White
val StarlingDarkTextSecondary = Color(0xFF8FA5B2) // Sea Glass Slate 400

// =========================================================================
// SYSTEM ALIASES FOR 100% BACKWARD COMPATIBILITY
// =========================================================================

// Coinpay aliases mapped to Starling/Wealthsimple tokens
val CoinpayCobaltPrimary = StarlingPineLight
val CoinpayCobaltDark = StarlingPinePrimary
val CoinpayCobaltLight = Color(0xFF1DA1B5)
val CoinpayCobaltSubtle = StarlingPineSubtle
val CoinpayCobaltNavy = StarlingPineDark

val CoinpaySolarYellow = StarlingSolarAmber
val CoinpaySolarLight = StarlingSolarLight
val CoinpaySolarContainer = StarlingSolarContainer

val CoinpayGreenIncome = StarlingGreenIncome
val CoinpayGreenContainer = StarlingGreenContainer
val CoinpayRedExpense = StarlingRedExpense
val CoinpayRedContainer = StarlingRedContainer
val CoinpayWarningAmber = StarlingWarningAmber

val CoinpayLightBg = WealthsimpleLightBg
val CoinpayLightCard = WealthsimpleLightCard
val CoinpayLightBorder = WealthsimpleLightBorder
val CoinpayLightTextPrimary = WealthsimpleLightTextPrimary
val CoinpayLightTextSecondary = WealthsimpleLightTextSecondary
val CoinpayLightTextMuted = WealthsimpleLightTextMuted

val CoinpayDarkBg = StarlingDarkBg
val CoinpayDarkCard = StarlingDarkCard
val CoinpayDarkCardSurface = StarlingDarkCardSurface
val CoinpayDarkBorder = StarlingDarkBorder
val CoinpayDarkTextPrimary = StarlingDarkTextPrimary
val CoinpayDarkTextSecondary = StarlingDarkTextSecondary

// Generic Fintech Aliases
val FintechMidnightBg = StarlingDarkBg
val FintechSecondaryBg = StarlingDarkCard
val FintechCardBg = StarlingDarkCard
val FintechCardSurface = StarlingDarkCardSurface
val FintechCardBorder = StarlingDarkBorder
val FintechBorderGlow = Color(0x1A00E676)

val FintechElectricViolet = StarlingPineLight
val FintechVioletLight = Color(0xFF1DA1B5)
val FintechVioletDark = StarlingPinePrimary
val FintechVioletContainer = StarlingPineSubtle

val FintechCyan = Color(0xFF00E676)
val FintechCyanLight = Color(0xFF34D399)
val FintechCyanContainer = StarlingMintContainer

val FintechEmerald = StarlingGreenIncome
val FintechEmeraldLight = Color(0xFF34D399)
val FintechEmeraldContainer = StarlingGreenContainer

val FintechAmber = StarlingSolarAmber
val FintechAmberLight = StarlingSolarLight
val FintechAmberContainer = StarlingSolarContainer

val FintechCoral = StarlingRedExpense
val FintechCoralLight = Color(0xFFF87171)
val FintechCoralContainer = StarlingRedContainer

val FintechTextPrimary = StarlingDarkTextPrimary
val FintechTextSecondary = StarlingDarkTextSecondary
val FintechTextMuted = Color(0xFF8FA5B2)

val FintechLightBg = WealthsimpleLightBg
val FintechLightCardBg = WealthsimpleLightCard
val FintechLightCardBorder = WealthsimpleLightBorder
val FintechLightTextPrimary = WealthsimpleLightTextPrimary
val FintechLightTextSecondary = WealthsimpleLightTextSecondary
val FintechLightViolet = StarlingPineMedium
val FintechLightVioletContainer = StarlingPineSubtle
val FintechLightCyan = StarlingPineLight

val ForestGreenPrimary = StarlingPinePrimary
val ForestGreenLight = StarlingPineLight
val ForestGreenContainer = StarlingPineSubtle
val EmeraldAccent = StarlingMintAccent

val WarmOffWhiteBg = WealthsimpleLightBg
val PureWhiteCard = WealthsimpleLightCard
val CardBorderColor = WealthsimpleLightBorder
val CharcoalTextPrimary = WealthsimpleLightTextPrimary
val MutedGreySecondary = WealthsimpleLightTextSecondary

val PositiveIncomeGreen = StarlingGreenIncome
val PositiveIncomeBg = StarlingGreenContainer
val ExpenseMutedRed = StarlingRedExpense
val ExpenseMutedRedBg = StarlingRedContainer
val WarningAmber = StarlingSolarAmber
val WarningAmberBg = StarlingSolarContainer

val DarkBg = StarlingDarkBg
val DarkCardBg = StarlingDarkCard
val DarkCardBorder = StarlingDarkBorder
val DarkTextPrimary = StarlingDarkTextPrimary
val DarkTextSecondary = StarlingDarkTextSecondary
val DarkPrimary = StarlingPineLight
val DarkPrimaryContainer = StarlingPineDark

// Category Pastel Tones (Monzo / Starling Rounded Squircle Badges)
val CategoryFood = Color(0xFFF97316)          // Warm Tangerine
val CategoryTravel = Color(0xFF0EA5E9)        // Sea Cyan
val CategoryShopping = Color(0xFFF59E0B)      // Warm Amber
val CategoryBills = Color(0xFF8B5CF6)         // Soft Violet
val CategoryHealth = Color(0xFFEC4899)        // Rose Red
val CategoryEducation = Color(0xFF10B981)     // Mint Jade
val CategoryEntertainment = Color(0xFF6366F1) // Indigo
val CategoryRent = Color(0xFF64748B)          // Cool Slate
val CategoryOther = Color(0xFF14B8A6)         // Teal
