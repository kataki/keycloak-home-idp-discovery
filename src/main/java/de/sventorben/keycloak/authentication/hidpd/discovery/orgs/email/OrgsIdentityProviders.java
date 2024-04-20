package de.sventorben.keycloak.authentication.hidpd.discovery.orgs.email;

import de.sventorben.keycloak.authentication.hidpd.discovery.email.Domain;
import de.sventorben.keycloak.authentication.hidpd.discovery.email.IdentityProviders;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.OrganizationModel;
import org.keycloak.models.UserModel;
import org.keycloak.organization.OrganizationProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class OrgsIdentityProviders implements IdentityProviders {

    @Override
    public List<IdentityProviderModel> candidatesForHomeIdp(AuthenticationFlowContext context, UserModel user) {
        OrganizationProvider orgProvider = context.getSession().getProvider(OrganizationProvider.class);
        if (user == null) {
            return Collections.emptyList();
        }
        if (orgProvider.isEnabled()) {
            OrganizationModel org = orgProvider.getByMember(user);
            if (org != null) {
                IdentityProviderModel orgIdp = org.getIdentityProvider();
                if (orgIdp != null) {
                    return Collections.singletonList(orgIdp);
                }
            }
        } else {
            // TODO: Log a warning
        }
        return Collections.emptyList();
    }

    @Override
    public List<IdentityProviderModel> withMatchingDomain(AuthenticationFlowContext context, List<IdentityProviderModel> candidates, Domain domain) {
        OrganizationProvider orgProvider = context.getSession().getProvider(OrganizationProvider.class);
        if (orgProvider.isEnabled()) {
            OrganizationModel org = orgProvider.getByDomainName(domain.getRawValue());
            if (org != null) {
                IdentityProviderModel orgIdp = org.getIdentityProvider();
                if (orgIdp != null) {
                    List<IdentityProviderModel> result = new ArrayList<>(candidates);
                    result.add(orgIdp);
                    return result;
                }
            }
        } else {
            // TODO: Log a warning
        }
        return candidates;
    }

}