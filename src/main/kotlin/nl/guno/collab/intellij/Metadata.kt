package nl.guno.collab.intellij

import com.smartbear.ccollab.datamodel.MetaDataDescription

data class Metadata (
        val overview: MetaDataDescription,
        val bugzillaInstantie: MetaDataDescription,
        val bugzillaNummer: MetaDataDescription,
        val fo: MetaDataDescription,
        val to: MetaDataDescription,
        val rnfo: MetaDataDescription,
        val rnto: MetaDataDescription,
        val rnMigratiePad: MetaDataDescription)
