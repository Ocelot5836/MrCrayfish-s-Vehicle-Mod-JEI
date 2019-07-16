package com.ocelot.vehicle.jei.plugin

import com.mrcrayfish.vehicle.client.gui.GuiFluidExtractor
import com.mrcrayfish.vehicle.client.gui.GuiFluidMixer
import com.mrcrayfish.vehicle.common.container.ContainerFluidExtractor
import com.mrcrayfish.vehicle.common.container.ContainerFluidMixer
import com.mrcrayfish.vehicle.common.container.ContainerWorkstation
import com.mrcrayfish.vehicle.init.ModBlocks
import com.mrcrayfish.vehicle.init.ModItems
import com.mrcrayfish.vehicle.item.ItemPart
import com.ocelot.vehicle.jei.VehicleModJei
import com.ocelot.vehicle.jei.plugin.fluidextractor.FluidExtractorCategory
import com.ocelot.vehicle.jei.plugin.fluidextractor.FluidExtractorRecipeMaker
import com.ocelot.vehicle.jei.plugin.fluidmixer.FluidMixerCategory
import com.ocelot.vehicle.jei.plugin.fluidmixer.FluidMixerRecipeMaker
import com.ocelot.vehicle.jei.plugin.workstation.VehicleCategory
import com.ocelot.vehicle.jei.plugin.workstation.VehicleRecipeMaker
import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.ISubtypeRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.ForgeRegistries

@JEIPlugin
class VehicleModJeiPlugin : IModPlugin {

    override fun registerItemSubtypes(subtypeRegistry: ISubtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(Item.getItemFromBlock(ModBlocks.VEHICLE_CRATE), object : ISubtypeRegistry.ISubtypeInterpreter {
            override fun apply(stack: ItemStack): String {
                if (stack.getSubCompound("BlockEntityTag") != null) {
                    return stack.getSubCompound("BlockEntityTag")!!.getString("vehicle")
                }
                return ISubtypeRegistry.ISubtypeInterpreter.NONE
            }
        })
    }

    override fun registerCategories(registry: IRecipeCategoryRegistration) {
        val guiHelper = registry.jeiHelpers.guiHelper
        registry.addRecipeCategories(
                FluidExtractorCategory(guiHelper),
                FluidMixerCategory(guiHelper),
                VehicleCategory(guiHelper)
        )
    }

    override fun register(registry: IModRegistry) {
        val jeiHelpers = registry.jeiHelpers

        registry.addRecipes(FluidExtractorRecipeMaker.getRecipes(jeiHelpers), VehicleModJei.FLUID_EXTRACTOR_UID)
        registry.addRecipes(FluidMixerRecipeMaker.getRecipes(jeiHelpers), VehicleModJei.FLUID_MIXER_UID)
        registry.addRecipes(VehicleRecipeMaker.getRecipes(jeiHelpers), VehicleModJei.VEHICLE_UID)

        registry.addRecipeClickArea(GuiFluidExtractor::class.java, 93, 35, 24, 17, VehicleModJei.FLUID_EXTRACTOR_UID, VanillaRecipeCategoryUid.FUEL)
        registry.addRecipeClickArea(GuiFluidMixer::class.java, 51, 26, 47, 46, VehicleModJei.FLUID_MIXER_UID, VanillaRecipeCategoryUid.FUEL)

        registry.addRecipeCatalyst(ItemStack(ModBlocks.FLUID_EXTRACTOR), VehicleModJei.FLUID_EXTRACTOR_UID, VanillaRecipeCategoryUid.FUEL)
        registry.addRecipeCatalyst(ItemStack(ModBlocks.FLUID_MIXER), VehicleModJei.FLUID_MIXER_UID, VanillaRecipeCategoryUid.FUEL)
        registry.addRecipeCatalyst(ItemStack(ModBlocks.WORKSTATION), VehicleModJei.VEHICLE_UID)

        val recipeTransferRegistry = registry.recipeTransferRegistry
        recipeTransferRegistry.addRecipeTransferHandler(ContainerFluidExtractor::class.java, VehicleModJei.FLUID_EXTRACTOR_UID, 1, 1, 2, 36)
        recipeTransferRegistry.addRecipeTransferHandler(ContainerFluidMixer::class.java, VehicleModJei.FLUID_MIXER_UID, 1, 1, 2, 36)

        val ingredientBlacklist = registry.jeiHelpers.ingredientBlacklist
        // Blacklist the models for vehicles
        ingredientBlacklist.addIngredientToBlacklist(ItemStack(ModItems.MODELS))
        for (item in ForgeRegistries.ITEMS) {
            if (item is ItemPart) {
                ingredientBlacklist.addIngredientToBlacklist(ItemStack(item))
            }
        }
    }
}