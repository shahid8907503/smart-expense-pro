

package com.smart.expense.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// COINPAY FINTECH FINANCE DESIGN SYSTEM
// (Inspired by Coinpay: Cobalt Blue, Clean White, Solar Gold & Low Glare)
// =========================================================================

// Coinpay Signature Brand Colors
val CoinpayCobaltPrimary = Color(0xFF2563EB)     // Electric Royal Cobalt Blue 600
val CoinpayCobaltDark = Color(0xFF1D4ED8)        // Deep Royal Blue 700 (Hero Card)
val CoinpayCobaltLight = Color(0xFF3B82F6)       // Vibrant Sky Cobalt 500
val CoinpayCobaltSubtle = Color(0xFFEFF6FF)      // Ultra-soft ice blue pill container (50)
val CoinpayCobaltNavy = Color(0xFF1E3A8A)        // Deep Midnight Cobalt 900

// Coinpay Secondary Accent (Solar Amber & Gold)
val CoinpaySolarYellow = Color(0xFFF59E0B)       // Warm Amber 500 (Spending charts & badges)
val CoinpaySolarLight = Color(0xFFFDE68A)        // Soft Buttercup 200
val CoinpaySolarContainer = Color(0xFFFEF3C7)    // Translucent Butter Container

// Financial Semantic Indicators
val CoinpayGreenIncome = Color(0xFF10B981)       // Vibrant Forest Jade (Positive Cashflow)
val CoinpayGreenContainer = Color(0xFFD1FAE5)    // Soft Jade Pill
val CoinpayRedExpense = Color(0xFFEF4444)        // Crisp Coral Red (Expenses)
val CoinpayRedContainer = Color(0xFFFEE2E2)      // Soft Coral Pill
val CoinpayWarningAmber = Color(0xFFF59E0B)      // Warning Amber

// Light Mode Canvas (Coinpay Pure White & Porcelain)
val CoinpayLightBg = Color(0xFFF8FAFC)           // Ultra-clean porcelain canvas (Slate 50)
val CoinpayLightCard = Color(0xFFFFFFFF)         // Crisp pure white card surface
val CoinpayLightBorder = Color(0xFFE2E8F0)       // Subtle 1px hairline border (Slate 200)
val CoinpayLightTextPrimary = Color(0xFF0F172A)  // Deep obsidian charcoal text (Slate 900)
val CoinpayLightTextSecondary = Color(0xFF64748B)// Slate 500 secondary
val CoinpayLightTextMuted = Color(0xFF94A3B8)    // Slate 400 muted

// Dark Mode Canvas (Matte Obsidian Charcoal - Low Glare)
val CoinpayDarkBg = Color(0xFF0B0F19)            // Deep Obsidian Canvas
val CoinpayDarkCard = Color(0xFF111827)          // Matte Slate Card (Gray 900)
val CoinpayDarkCardSurface = Color(0xFF1E293B)   // Raised slate surface (Slate 800)
val CoinpayDarkBorder = Color(0xFF334155)        // Slate 700 border
val CoinpayDarkTextPrimary = Color(0xFFF8FAFC)   // Crisp White
val CoinpayDarkTextSecondary = Color(0xFF94A3B8) // Slate 400

// Backward Compatibility Aliases for Design System
val FintechMidnightBg = CoinpayDarkBg
val FintechSecondaryBg = Color(0xFF111827)
val FintechCardBg = CoinpayDarkCard
val FintechCardSurface = CoinpayDarkCardSurface
val FintechCardBorder = CoinpayDarkBorder
val FintechBorderGlow = Color(0x1A2563EB)

val FintechElectricViolet = CoinpayCobaltPrimary
val FintechVioletLight = CoinpayCobaltLight
val FintechVioletDark = CoinpayCobaltDark
val FintechVioletContainer = CoinpayCobaltSubtle

val FintechCyan = CoinpayCobaltPrimary
val FintechCyanLight = CoinpayCobaltLight
val FintechCyanContainer = CoinpayCobaltSubtle

val FintechEmerald = CoinpayGreenIncome
val FintechEmeraldLight = Color(0xFF34D399)
val FintechEmeraldContainer = CoinpayGreenContainer

val FintechAmber = CoinpaySolarYellow
val FintechAmberLight = Color(0xFFFBBF24)
val FintechAmberContainer = CoinpaySolarContainer

val FintechCoral = CoinpayRedExpense
val FintechCoralLight = Color(0xFFF87171)
val FintechCoralContainer = CoinpayRedContainer

val FintechTextPrimary = CoinpayDarkTextPrimary
val FintechTextSecondary = CoinpayDarkTextSecondary
val FintechTextMuted = Color(0xFF64748B)

val FintechLightBg = CoinpayLightBg
val FintechLightCardBg = CoinpayLightCard
val FintechLightCardBorder = CoinpayLightBorder
val FintechLightTextPrimary = CoinpayLightTextPrimary
val FintechLightTextSecondary = CoinpayLightTextSecondary
val FintechLightViolet = CoinpayCobaltPrimary
val FintechLightVioletContainer = CoinpayCobaltSubtle
val FintechLightCyan = CoinpayCobaltLight

val ForestGreenPrimary = CoinpayCobaltPrimary
val ForestGreenLight = CoinpayCobaltLight
val ForestGreenContainer = CoinpayCobaltSubtle
val EmeraldAccent = CoinpayCobaltPrimary

val WarmOffWhiteBg = CoinpayLightBg
val PureWhiteCard = CoinpayLightCard
val CardBorderColor = CoinpayLightBorder
val CharcoalTextPrimary = CoinpayLightTextPrimary
val MutedGreySecondary = CoinpayLightTextSecondary

val PositiveIncomeGreen = CoinpayGreenIncome
val PositiveIncomeBg = CoinpayGreenContainer
val ExpenseMutedRed = CoinpayRedExpense
val ExpenseMutedRedBg = CoinpayRedContainer
val WarningAmber = CoinpaySolarYellow
val WarningAmberBg = CoinpaySolarContainer

val DarkBg = CoinpayDarkBg
val DarkCardBg = CoinpayDarkCard
val DarkCardBorder = CoinpayDarkBorder
val DarkTextPrimary = CoinpayDarkTextPrimary
val DarkTextSecondary = CoinpayDarkTextSecondary
val DarkPrimary = CoinpayCobaltLight
val DarkPrimaryContainer = Color(0xFF1E3A8A)

// Category Pastel Tones (Coinpay Circular Badge Style)
val CategoryFood = Color(0xFFFB923C)         // Soft Coral Orange
val CategoryTravel = Color(0xFF3B82F6)       // Royal Blue
val CategoryShopping = Color(0xFFF59E0B)     // Solar Amber
val CategoryBills = Color(0xFF8B5CF6)        // Lavender Violet
val CategoryHealth = Color(0xFFEC4899)       // Pink Rose
val CategoryEducation = Color(0xFF6366F1)    // Indigo
val CategoryEntertainment = Color(0xFFA855F7)// Orchid
val CategoryRent = Color(0xFF64748B)         // Slate
val CategoryOther = Color(0xFF0EA5E9)        // Sky Blue
